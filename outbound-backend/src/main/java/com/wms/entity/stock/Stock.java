package com.wms.entity.stock;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库存实体
 * 记录物料的实时库存
 */
@Data
@TableName("wms_stock")
public class Stock {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 物料ID
     */
    private Long materialId;

    /**
     * 当前库存数量
     */
    private BigDecimal quantity;

    /**
     * 可用库存数量
     */
    private BigDecimal availableQuantity;

    /**
     * 锁定库存数量（已被打包单占用但未出库）
     */
    private BigDecimal lockedQuantity;

    /**
     * 预警库存阈值
     */
    private BigDecimal warningThreshold;

    /**
     * 仓库位置
     */
    private String warehouseLocation;

    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdateTime;

    /**
     * 版本号（乐观锁）
     */
    private Integer version;
}
