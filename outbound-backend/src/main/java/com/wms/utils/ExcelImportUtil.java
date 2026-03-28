package com.wms.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Excel导入工具类
 * 支持并发批量处理大数据量导入
 */
@Slf4j
@Component
public class ExcelImportUtil {

    /**
     * 默认批次大小
     */
    private static final int DEFAULT_BATCH_SIZE = 500;

    /**
     * 线程池大小
     */
    private static final int THREAD_POOL_SIZE = 4;

    /**
     * 导入结果
     */
    @Data
    public static class ImportResult<T> {
        /**
         * 总数
         */
        private int total;
        /**
         * 成功数
         */
        private int success;
        /**
         * 失败数
         */
        private int fail;
        /**
         * 失败详情
         */
        private List<FailRecord> failList = new ArrayList<>();

        @Data
        public static class FailRecord {
            private Integer row;
            private String data;
            private String reason;

            public FailRecord(Integer row, String data, String reason) {
                this.row = row;
                this.data = data;
                this.reason = reason;
            }
        }
    }

    /**
     * 导入监听器的上下文
     */
    private static class ImportContext<T> {
        private final List<T> dataList = new ArrayList<>();
        private final ImportResult<T> result = new ImportResult<>();
        private final Consumer<List<T>> batchProcessor;
        private final int batchSize;
        private final CountDownLatch latch;

        ImportContext(Consumer<List<T>> batchProcessor, int batchSize, CountDownLatch latch) {
            this.batchProcessor = batchProcessor;
            this.batchSize = batchSize;
            this.latch = latch;
        }
    }

    /**
     * 并发导入Excel
     *
     * @param file           上传的文件
     * @param clazz          数据类
     * @param batchProcessor 批量处理函数
     * @param <T>            数据类型
     * @return 导入结果
     */
    public <T> ImportResult<T> concurrentImport(MultipartFile file, Class<T> clazz, 
                                                 Consumer<List<T>> batchProcessor) throws IOException {
        return concurrentImport(file, clazz, batchProcessor, DEFAULT_BATCH_SIZE);
    }

    /**
     * 并发导入Excel（指定批次大小）
     *
     * @param file           上传的文件
     * @param clazz          数据类
     * @param batchProcessor 批量处理函数
     * @param batchSize      批次大小
     * @param <T>            数据类型
     * @return 导入结果
     */
    public <T> ImportResult<T> concurrentImport(MultipartFile file, Class<T> clazz,
                                                 Consumer<List<T>> batchProcessor, int batchSize) throws IOException {
        
        log.info("开始并发导入Excel，文件名: {}，批次大小: {}", file.getOriginalFilename(), batchSize);
        
        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        CountDownLatch latch = new CountDownLatch(1);
        ImportContext<T> context = new ImportContext<>(batchProcessor, batchSize, latch);

        try {
            // 读取Excel
            EasyExcel.read(file.getInputStream(), clazz, new ImportListener<>(context, executor))
                    .sheet()
                    .doRead();

            // 处理剩余数据
            if (!context.dataList.isEmpty()) {
                submitBatchTask(executor, new ArrayList<>(context.dataList), context);
                context.dataList.clear();
            }

            latch.countDown();
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.MINUTES);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("导入任务被中断", e);
            throw new RuntimeException("导入任务被中断", e);
        } finally {
            if (!executor.isShutdown()) {
                executor.shutdownNow();
            }
        }

        log.info("导入完成，总数: {}，成功: {}，失败: {}", 
                context.result.getTotal(), context.result.getSuccess(), context.result.getFail());
        return context.result;
    }

    /**
     * 提交批量处理任务
     */
    private <T> void submitBatchTask(ExecutorService executor, List<T> batch, ImportContext<T> context) {
        executor.submit(() -> {
            try {
                context.batchProcessor.accept(batch);
                synchronized (context.result) {
                    context.result.setSuccess(context.result.getSuccess() + batch.size());
                }
            } catch (Exception e) {
                log.error("批次处理失败", e);
                synchronized (context.result) {
                    for (T item : batch) {
                        context.result.getFailList().add(new ImportResult.FailRecord(
                                null, item.toString(), e.getMessage()));
                    }
                    context.result.setFail(context.result.getFail() + batch.size());
                }
            }
        });
    }

    /**
     * 导入监听器
     */
    private static class ImportListener<T> extends AnalysisEventListener<T> {
        private final ImportContext<T> context;
        private final ExecutorService executor;

        ImportListener(ImportContext<T> context, ExecutorService executor) {
            this.context = context;
            this.executor = executor;
        }

        @Override
        public void invoke(T data, AnalysisContext analysisContext) {
            synchronized (context) {
                context.dataList.add(data);
                context.result.setTotal(context.result.getTotal() + 1);

                // 达到批次大小，提交处理
                if (context.dataList.size() >= context.batchSize) {
                    List<T> batch = new ArrayList<>(context.dataList);
                    context.dataList.clear();
                    submitBatchTask(executor, batch, context);
                }
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext analysisContext) {
            // 所有数据解析完成
        }

        @Override
        public void onException(Exception exception, AnalysisContext context) {
            log.error("解析失败: {}", exception.getMessage());
            if (exception instanceof ExcelDataConvertException) {
                ExcelDataConvertException convertException = (ExcelDataConvertException) exception;
                log.error("第{}行，第{}列解析异常", convertException.getRowIndex(), 
                        convertException.getColumnIndex());
            }
        }
    }

    /**
     * 同步导入（小数据量使用）
     *
     * @param file           上传的文件
     * @param clazz          数据类
     * @param rowProcessor   单行处理函数
     * @param <T>            数据类型
     * @return 导入结果
     */
    public <T> ImportResult<T> syncImport(MultipartFile file, Class<T> clazz,
                                          java.util.function.BiConsumer<T, ImportResult<T>> rowProcessor) throws IOException {
        
        ImportResult<T> result = new ImportResult<>();
        
        EasyExcel.read(file.getInputStream(), clazz, new AnalysisEventListener<T>() {
            @Override
            public void invoke(T data, AnalysisContext context) {
                result.setTotal(result.getTotal() + 1);
                try {
                    rowProcessor.accept(data, result);
                    result.setSuccess(result.getSuccess() + 1);
                } catch (Exception e) {
                    result.setFail(result.getFail() + 1);
                    result.getFailList().add(new ImportResult.FailRecord(
                            context.readRowHolder().getRowIndex(), data.toString(), e.getMessage()));
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                // 完成
            }
        }).sheet().doRead();

        return result;
    }
}
