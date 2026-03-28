package com.wms.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 打包记录视图对象
 */
@Data
public class PackingRecordVO {

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 货单编号
     */
    private String orderNo;

    /**
     * 集装箱号
     */
    private String containerNo;

    /**
     * 产品条码
     */
    private String productBarcode;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 打包类型
     */
    private Integer packingType;

    /**
     * 打包类型描述
     */
    private String packingTypeDesc;

    /**
     * 打包数量
     */
    private Integer quantity;

    /**
     * 标签是否打印
     */
    private Boolean labelPrinted;

    /**
     * 打包员
     */
    private String packerName;

    /**
     * 打包时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime packingTime;

    /**
     * 备注
     */
    private String remark;
}
