package com.wms.service;

import com.wms.entity.stock.Stock;
import com.wms.entity.stock.StockRecord;

import java.math.BigDecimal;

/**
 * 库存服务接口
 */
public interface StockService {

    /**
     * 初始化库存（物料新增时调用）
     */
    void initStock(Long materialId);

    /**
     * 入库
     */
    void inbound(Long materialId, BigDecimal quantity, String bizType, Long bizId, String remark);

    /**
     * 出库
     */
    void outbound(Long materialId, BigDecimal quantity, String bizType, Long bizId, String remark);

    /**
     * 锁定库存（打包前锁定所需物料）
     */
    void lockStock(Long materialId, BigDecimal quantity, Long orderId);

    /**
     * 解锁库存（取消打包时解锁）
     */
    void unlockStock(Long materialId, BigDecimal quantity, Long orderId);

    /**
     * 扣减锁定库存（确认打包完成出库）
     */
    void deductLockedStock(Long materialId, BigDecimal quantity, Long orderId);

    /**
     * 检查库存是否充足
     */
    boolean checkStock(Long materialId, BigDecimal requiredQuantity);

    /**
     * 获取库存信息
     */
    Stock getStockByMaterialId(Long materialId);

    /**
     * 设置库存预警阈值
     */
    void setWarningThreshold(Long materialId, BigDecimal threshold);
}
