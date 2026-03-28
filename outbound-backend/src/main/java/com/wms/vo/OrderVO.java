package com.wms.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 货单视图对象
 */
@Data
public class OrderVO {

    /**
     * 货单ID
     */
    private Long id;

    /**
     * 货单编号
     */
    private String orderNo;

    /**
     * 货单类型
     */
    private Integer orderType;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 客户联系人
     */
    private String customerContact;

    /**
     * 客户电话
     */
    private String customerPhone;

    /**
     * 目的地
     */
    private String destination;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 状态标签类型（前端展示用）
     */
    private String statusTagType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 客柜列表
     */
    private List<ContainerVO> containers;

    /**
     * 产品明细
     */
    private List<OrderProductVO> products;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
