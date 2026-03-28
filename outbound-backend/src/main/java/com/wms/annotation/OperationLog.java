package com.wms.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 用于标记需要记录操作日志的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 操作类型
     */
    OperationType type();

    /**
     * 操作描述
     */
    String desc() default "";

    /**
     * 是否记录请求参数
     */
    boolean recordParams() default true;

    /**
     * 是否记录响应结果
     */
    boolean recordResult() default false;

    /**
     * 操作类型枚举
     */
    enum OperationType {
        /**
         * 查询
         */
        QUERY,
        
        /**
         * 新增
         */
        CREATE,
        
        /**
         * 修改
         */
        UPDATE,
        
        /**
         * 删除
         */
        DELETE,
        
        /**
         * 导入
         */
        IMPORT,
        
        /**
         * 导出
         */
        EXPORT,
        
        /**
         * 登录
         */
        LOGIN,
        
        /**
         * 登出
         */
        LOGOUT,
        
        /**
         * 其他
         */
        OTHER
    }
}
