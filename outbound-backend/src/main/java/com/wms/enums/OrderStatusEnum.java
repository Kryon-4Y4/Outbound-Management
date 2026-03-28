package com.wms.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * 货单状态枚举
 */
@Getter
public enum OrderStatusEnum {

    PENDING(0, "待处理", "warning"),
    PROCESSING(1, "处理中", "primary"),
    COMPLETED(2, "已完成", "success"),
    CANCELLED(3, "已取消", "info");

    private final Integer code;
    private final String desc;
    private final String tagType;

    OrderStatusEnum(Integer code, String desc, String tagType) {
        this.code = code;
        this.desc = desc;
        this.tagType = tagType;
    }

    /**
     * 根据code获取枚举
     */
    public static Optional<OrderStatusEnum> getByCode(Integer code) {
        return Arrays.stream(values())
                .filter(e -> e.getCode().equals(code))
                .findFirst();
    }

    /**
     * 获取状态描述
     */
    public static String getDescByCode(Integer code) {
        return getByCode(code).map(OrderStatusEnum::getDesc).orElse("未知");
    }
}
