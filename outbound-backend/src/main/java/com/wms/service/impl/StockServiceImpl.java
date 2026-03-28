package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.entity.stock.Stock;
import com.wms.entity.stock.StockRecord;
import com.wms.exception.BusinessException;
import com.wms.exception.ErrorCode;
import com.wms.mapper.StockMapper;
import com.wms.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库存服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockMapper stockMapper;
    // private final StockRecordMapper stockRecordMapper; // 如需记录流水可添加

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initStock(Long materialId) {
        Stock stock = new Stock();
        stock.setMaterialId(materialId);
        stock.setQuantity(BigDecimal.ZERO);
        stock.setAvailableQuantity(BigDecimal.ZERO);
        stock.setLockedQuantity(BigDecimal.ZERO);
        stock.setWarningThreshold(new BigDecimal("100")); // 默认预警值
        stock.setLastUpdateTime(LocalDateTime.now());
        stock.setVersion(0);
        stockMapper.insert(stock);
        log.info("初始化库存成功，物料ID: {}", materialId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void inbound(Long materialId, BigDecimal quantity, String bizType, Long bizId, String remark) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("入库数量必须大于0");
        }
        
        int rows = stockMapper.increaseStock(materialId, quantity);
        if (rows == 0) {
            // 库存记录不存在，先初始化
            initStock(materialId);
            stockMapper.increaseStock(materialId, quantity);
        }
        
        // 记录流水（可选）
        saveStockRecord(materialId, 1, quantity, bizType, bizId, remark);
        log.info("入库成功，物料ID: {}，数量: {}", materialId, quantity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void outbound(Long materialId, BigDecimal quantity, String bizType, Long bizId, String remark) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("出库数量必须大于0");
        }
        
        int rows = stockMapper.decreaseStock(materialId, quantity);
        if (rows == 0) {
            throw new BusinessException(ErrorCode.ERROR.getCode(), "库存不足或库存记录不存在");
        }
        
        saveStockRecord(materialId, 2, quantity.negate(), bizType, bizId, remark);
        log.info("出库成功，物料ID: {}，数量: {}", materialId, quantity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void lockStock(Long materialId, BigDecimal quantity, Long orderId) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        
        int rows = stockMapper.lockStock(materialId, quantity);
        if (rows == 0) {
            throw new BusinessException(ErrorCode.ERROR.getCode(), "库存不足，无法锁定");
        }
        
        saveStockRecord(materialId, 3, quantity, "ORDER", orderId, "打包锁定库存");
        log.info("锁定库存成功，物料ID: {}，数量: {}", materialId, quantity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlockStock(Long materialId, BigDecimal quantity, Long orderId) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        
        int rows = stockMapper.unlockStock(materialId, quantity);
        if (rows == 0) {
            log.warn("解锁库存失败，物料ID: {}，可能锁定数量不足", materialId);
            return;
        }
        
        saveStockRecord(materialId, 4, quantity.negate(), "ORDER", orderId, "取消打包解锁库存");
        log.info("解锁库存成功，物料ID: {}，数量: {}", materialId, quantity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductLockedStock(Long materialId, BigDecimal quantity, Long orderId) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        
        int rows = stockMapper.deductLockedStock(materialId, quantity);
        if (rows == 0) {
            throw new BusinessException(ErrorCode.ERROR.getCode(), "锁定库存不足");
        }
        
        saveStockRecord(materialId, 2, quantity.negate(), "ORDER", orderId, "打包完成出库");
        log.info("扣减锁定库存成功，物料ID: {}，数量: {}", materialId, quantity);
    }

    @Override
    public boolean checkStock(Long materialId, BigDecimal requiredQuantity) {
        Stock stock = getStockByMaterialId(materialId);
        if (stock == null) {
            return false;
        }
        return stock.getAvailableQuantity().compareTo(requiredQuantity) >= 0;
    }

    @Override
    public Stock getStockByMaterialId(Long materialId) {
        LambdaQueryWrapper<Stock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Stock::getMaterialId, materialId);
        return stockMapper.selectOne(wrapper);
    }

    @Override
    public void setWarningThreshold(Long materialId, BigDecimal threshold) {
        Stock stock = getStockByMaterialId(materialId);
        if (stock == null) {
            throw new BusinessException("库存记录不存在");
        }
        stock.setWarningThreshold(threshold);
        stockMapper.updateById(stock);
    }

    /**
     * 保存库存变动记录
     */
    private void saveStockRecord(Long materialId, Integer changeType, BigDecimal quantity, 
                                  String bizType, Long bizId, String remark) {
        // 如需实现，注入 StockRecordMapper 并保存记录
        // StockRecord record = new StockRecord();
        // ... 设置字段
        // stockRecordMapper.insert(record);
    }
}
