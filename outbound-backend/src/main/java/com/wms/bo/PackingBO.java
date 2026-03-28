package com.wms.bo;

import lombok.Data;

import java.util.List;

/**
 * 打包业务对象
 * 用于打包过程中的业务逻辑处理
 */
@Data
public class PackingBO {

    /**
     * 货单ID
     */
    private Long orderId;

    /**
     * 客柜ID
     */
    private Long containerId;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 产品条码
     */
    private String productBarcode;

    /**
     * 打包数量
     */
    private Integer quantity;

    /**
     * 打包类型：1-传送带 2-脚装
     */
    private Integer packingType;

    /**
     * 打包员ID
     */
    private Long packerId;

    /**
     * 是否立即打印标签
     */
    private Boolean printLabel;

    /**
     * 本次打包所需物料清单
     */
    private List<MaterialRequirement> materialRequirements;

    /**
     * 备注
     */
    private String remark;

    /**
     * 物料需求内部类
     */
    @Data
    public static class MaterialRequirement {
        /**
         * 物料ID
         */
        private Long materialId;

        /**
         * 物料名称
         */
        private String materialName;

        /**
         * 所需数量
         */
        private Integer requiredQuantity;

        /**
         * 打包位置
         */
        private String packingLocation;
    }
}
