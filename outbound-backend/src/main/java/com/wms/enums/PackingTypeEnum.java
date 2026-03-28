package com.wms.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * 打包类型枚举
 */
@Getter
public enum PackingTypeEnum {

    CONVEYOR(1, "传送带打包"),
    MANUAL(2, "脚装打包");

    private final Integer code;
    private final String desc;

    PackingTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static Optional<PackingTypeEnum> getByCode(Integer code) {
        return Arrays.stream(values())
                .filter(e -> e.getCode().equals(code))
                .findFirst();
    }

    public static String getDescByCode(Integer code) {
        return getByCode(code).map(PackingTypeEnum::getDesc).orElse("未知");
    }
}
