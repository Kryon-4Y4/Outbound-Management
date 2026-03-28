package com.wms.entity.stock;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库存变动记录
 * 记录所有库存变动流水
 */
@Data
@TableName("wms_stock_record")
public class StockRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 物料ID
     */
    private Long materialId;

    /**
     * 变动类型：1-入库 2-出库 3-锁定 4-解锁 5-盘点调整
     */
    private Integer changeType;

    /**
     * 变动数量（正数表示增加，负数表示减少）
     */
    private BigDecimal changeQuantity;

    /**
     * 变动前数量
     */
    private BigDecimal beforeQuantity;

    /**
     * 变动后数量
     */
    private BigDecimal afterQuantity;

    /**
     * 关联业务类型：ORDER-货单 PACKING-打包
     */
    private String bizType;

    /**
     * 关联业务ID
     */
    private Long bizId;

    /**
     * 操作人
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
