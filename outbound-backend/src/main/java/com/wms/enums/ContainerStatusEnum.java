package com.wms.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * 客柜状态枚举
 */
@Getter
public enum ContainerStatusEnum {

    EMPTY(0, "空箱", "info"),
    LOADING(1, "装载中", "primary"),
    FULL(2, "满载", "warning"),
    SEALED(3, "已封箱", "success");

    private final Integer code;
    private final String desc;
    private final String tagType;

    ContainerStatusEnum(Integer code, String desc, String tagType) {
        this.code = code;
        this.desc = desc;
        this.tagType = tagType;
    }

    public static Optional<ContainerStatusEnum> getByCode(Integer code) {
        return Arrays.stream(values())
                .filter(e -> e.getCode().equals(code))
                .findFirst();
    }

    public static String getDescByCode(Integer code) {
        return getByCode(code).map(ContainerStatusEnum::getDesc).orElse("未知");
    }
}
