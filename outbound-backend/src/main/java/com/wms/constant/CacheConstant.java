package com.wms.constant;

/**
 * 缓存常量定义
 */
public final class CacheConstant {

    private CacheConstant() {
        // 禁止实例化
    }

    /**
     * 缓存前缀
     */
    public static final String CACHE_PREFIX = "wms:";

    /**
     * 用户缓存 key: wms:user:{userId}
     */
    public static final String USER_CACHE_KEY = CACHE_PREFIX + "user:";

    /**
     * 用户权限缓存 key: wms:permissions:{userId}
     */
    public static final String PERMISSIONS_CACHE_KEY = CACHE_PREFIX + "permissions:";

    /**
     * 字典数据缓存 key: wms:dict:{dictType}
     */
    public static final String DICT_CACHE_KEY = CACHE_PREFIX + "dict:";

    /**
     * 物料缓存 key: wms:material:{materialId}
     */
    public static final String MATERIAL_CACHE_KEY = CACHE_PREFIX + "material:";

    /**
     * 产品缓存 key: wms:product:{productId}
     */
    public static final String PRODUCT_CACHE_KEY = CACHE_PREFIX + "product:";

    /**
     * 货单缓存 key: wms:order:{orderId}
     */
    public static final String ORDER_CACHE_KEY = CACHE_PREFIX + "order:";

    /**
     * 默认缓存过期时间（秒）
     */
    public static final long DEFAULT_EXPIRE = 3600;

    /**
     * 用户缓存过期时间（秒）
     */
    public static final long USER_EXPIRE = 7200;
}
