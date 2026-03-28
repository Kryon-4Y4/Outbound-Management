package com.wms.vo;

import lombok.Data;

/**
 * 客柜产品视图对象
 */
@Data
public class ContainerProductVO {

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
     * 计划数量
     */
    private Integer quantity;

    /**
     * 已打包数量
     */
    private Integer packedQuantity;

    /**
     * 单位
     */
    private String unit;
}
