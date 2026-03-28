package com.wms.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 客柜视图对象
 */
@Data
public class ContainerVO {

    /**
     * 客柜ID
     */
    private Long id;

    /**
     * 集装箱号
     */
    private String containerNo;

    /**
     * 箱型
     */
    private String containerType;

    /**
     * 封条号
     */
    private String sealNo;

    /**
     * 最大载重
     */
    private BigDecimal maxWeight;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 状态标签类型
     */
    private String statusTagType;

    /**
     * 产品列表
     */
    private List<ContainerProductVO> products;
}
