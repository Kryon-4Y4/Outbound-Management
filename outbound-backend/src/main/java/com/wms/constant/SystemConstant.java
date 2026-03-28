package com.wms.constant;

/**
 * 系统常量定义
 */
public final class SystemConstant {

    private SystemConstant() {
        // 禁止实例化
    }

    // ==================== 分页常量 ====================
    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE_NUM = 1;
    
    /**
     * 默认每页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    /**
     * 最大每页大小
     */
    public static final int MAX_PAGE_SIZE = 1000;

    // ==================== 状态常量 ====================
    /**
     * 状态：启用
     */
    public static final int STATUS_ENABLED = 1;
    
    /**
     * 状态：禁用
     */
    public static final int STATUS_DISABLED = 0;

    /**
     * 删除标志：未删除
     */
    public static final int NOT_DELETED = 0;
    
    /**
     * 删除标志：已删除
     */
    public static final int DELETED = 1;

    // ==================== JWT常量 ====================
    /**
     * Token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";
    
    /**
     * Token请求头
     */
    public static final String TOKEN_HEADER = "Authorization";

    // ==================== 货单状态 ====================
    /**
     * 货单状态：待处理
     */
    public static final int ORDER_STATUS_PENDING = 0;
    
    /**
     * 货单状态：处理中
     */
    public static final int ORDER_STATUS_PROCESSING = 1;
    
    /**
     * 货单状态：已完成
     */
    public static final int ORDER_STATUS_COMPLETED = 2;
    
    /**
     * 货单状态：已取消
     */
    public static final int ORDER_STATUS_CANCELLED = 3;

    // ==================== 客柜状态 ====================
    /**
     * 客柜状态：空箱
     */
    public static final int CONTAINER_STATUS_EMPTY = 0;
    
    /**
     * 客柜状态：装载中
     */
    public static final int CONTAINER_STATUS_LOADING = 1;
    
    /**
     * 客柜状态：满载
     */
    public static final int CONTAINER_STATUS_FULL = 2;
    
    /**
     * 客柜状态：已封箱
     */
    public static final int CONTAINER_STATUS_SEALED = 3;

    // ==================== 打包类型 ====================
    /**
     * 打包类型：传送带
     */
    public static final int PACKING_TYPE_CONVEYOR = 1;
    
    /**
     * 打包类型：脚装
     */
    public static final int PACKING_TYPE_MANUAL = 2;

    // ==================== 菜单类型 ====================
    /**
     * 菜单类型：目录
     */
    public static final int MENU_TYPE_DIR = 1;
    
    /**
     * 菜单类型：菜单
     */
    public static final int MENU_TYPE_MENU = 2;
    
    /**
     * 菜单类型：按钮
     */
    public static final int MENU_TYPE_BUTTON = 3;

    // ==================== 文件上传常量 ====================
    /**
     * 允许上传的文件类型
     */
    public static final String ALLOWED_FILE_TYPES = "xlsx,xls,csv";
    
    /**
     * 最大文件大小 (10MB)
     */
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
}
