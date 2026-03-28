package com.wms.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 产品物料关联视图对象
 */
@Data
public class ProductMaterialVO {

    /**
     * 物料ID
     */
    private Long materialId;

    /**
     * 物料编码
     */
    private String materialCode;

    /**
     * 物料名称
     */
    private String materialName;

    /**
     * 规格型号
     */
    private String specification;

    /**
     * 计量单位
     */
    private String unit;

    /**
     * 所需数量
     */
    private BigDecimal quantity;

    /**
     * 排序
     */
    private Integer sortOrder;
}
