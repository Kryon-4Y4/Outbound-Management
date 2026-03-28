package com.wms.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * 标签类型枚举
 */
@Getter
public enum LabelTypeEnum {

    MATERIAL(1, "物料标签"),
    PRODUCT(2, "产品标签"),
    BOX(3, "箱标签");

    private final Integer code;
    private final String desc;

    LabelTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static Optional<LabelTypeEnum> getByCode(Integer code) {
        return Arrays.stream(values())
                .filter(e -> e.getCode().equals(code))
                .findFirst();
    }
}
