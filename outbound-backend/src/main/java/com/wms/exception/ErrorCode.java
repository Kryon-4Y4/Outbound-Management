package com.wms.exception;

import lombok.Getter;

/**
 * 错误码枚举
 * 统一定义系统错误码，便于维护和国际化
 */
@Getter
public enum ErrorCode {

    // 系统级别错误 1xx
    SUCCESS(200, "操作成功"),
    ERROR(500, "系统内部错误"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权，请重新登录"),
    FORBIDDEN(403, "拒绝访问"),
    NOT_FOUND(404, "资源不存在"),

    // 用户模块错误 10xx
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    USER_PASSWORD_ERROR(1003, "用户名或密码错误"),
    USER_ACCOUNT_DISABLED(1004, "账号已被禁用"),
    USER_OLD_PASSWORD_ERROR(1005, "原密码错误"),

    // 物料模块错误 20xx
    MATERIAL_NOT_FOUND(2001, "物料不存在"),
    MATERIAL_CODE_EXISTS(2002, "物料编码已存在"),
    MATERIAL_IMPORT_ERROR(2003, "物料导入失败"),

    // 产品模块错误 30xx
    PRODUCT_NOT_FOUND(3001, "产品不存在"),
    PRODUCT_BARCODE_EXISTS(3002, "产品条码已存在"),
    PRODUCT_HAS_ORDERS(3003, "产品已关联货单，无法删除"),

    // 货单模块错误 40xx
    ORDER_NOT_FOUND(4001, "货单不存在"),
    ORDER_NO_EXISTS(4002, "货单编号已存在"),
    ORDER_STATUS_ERROR(4003, "货单状态不允许此操作"),

    // 客柜模块错误 50xx
    CONTAINER_NOT_FOUND(5001, "客柜不存在"),
    CONTAINER_NO_EXISTS(5002, "集装箱号已存在"),
    CONTAINER_FULL(5003, "客柜已满"),

    // 打包模块错误 60xx
    PACKING_PRODUCT_NOT_IN_ORDER(6001, "产品不在当前货单中"),
    PACKING_QUANTITY_EXCEED(6002, "打包数量超过计划数量"),
    PACKING_CONTAINER_CLOSED(6003, "客柜已封箱，无法打包"),

    // 标签模块错误 70xx
    LABEL_PRINT_ERROR(7001, "标签打印失败"),
    LABEL_TEMPLATE_NOT_FOUND(7002, "标签模板不存在");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
