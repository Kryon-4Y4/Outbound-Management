package com.wms.vo;

import lombok.Data;

/**
 * 货单产品明细视图对象
 */
@Data
public class OrderProductVO {

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 产品条码
     */
    private String productBarcode;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 单位
     */
    private String unit;

    /**
     * 计划数量
     */
    private Integer quantity;

    /**
     * 已打包数量
     */
    private Integer packedQuantity;

    /**
     * 剩余数量
     */
    private Integer remainQuantity;

    /**
     * 打包进度百分比
     */
    private Integer progress;

    /**
     * 备注
     */
    private String remark;
}
