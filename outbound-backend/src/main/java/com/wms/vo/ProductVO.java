package com.wms.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 产品视图对象
 */
@Data
public class ProductVO {

    /**
     * 产品ID
     */
    private Long id;

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
     * 颜色
     */
    private String color;

    /**
     * 规格
     */
    private String specification;

    /**
     * 计量单位
     */
    private String unit;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * BOM物料清单
     */
    private List<ProductMaterialVO> bomList;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
