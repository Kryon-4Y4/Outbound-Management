package com.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.entity.stock.Stock;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

/**
 * 库存Mapper
 */
public interface StockMapper extends BaseMapper<Stock> {

    /**
     * 增加库存（入库）
     */
    @Update("UPDATE wms_stock SET quantity = quantity + #{quantity}, " +
            "available_quantity = available_quantity + #{quantity}, " +
            "last_update_time = NOW(), version = version + 1 " +
            "WHERE material_id = #{materialId}")
    int increaseStock(@Param("materialId") Long materialId, @Param("quantity") BigDecimal quantity);

    /**
     * 扣减库存（出库）
     */
    @Update("UPDATE wms_stock SET quantity = quantity - #{quantity}, " +
            "available_quantity = available_quantity - #{quantity}, " +
            "last_update_time = NOW(), version = version + 1 " +
            "WHERE material_id = #{materialId} AND available_quantity >= #{quantity}")
    int decreaseStock(@Param("materialId") Long materialId, @Param("quantity") BigDecimal quantity);

    /**
     * 锁定库存
     */
    @Update("UPDATE wms_stock SET locked_quantity = locked_quantity + #{quantity}, " +
            "available_quantity = available_quantity - #{quantity}, " +
            "last_update_time = NOW(), version = version + 1 " +
            "WHERE material_id = #{materialId} AND available_quantity >= #{quantity}")
    int lockStock(@Param("materialId") Long materialId, @Param("quantity") BigDecimal quantity);

    /**
     * 解锁库存
     */
    @Update("UPDATE wms_stock SET locked_quantity = locked_quantity - #{quantity}, " +
            "available_quantity = available_quantity + #{quantity}, " +
            "last_update_time = NOW(), version = version + 1 " +
            "WHERE material_id = #{materialId} AND locked_quantity >= #{quantity}")
    int unlockStock(@Param("materialId") Long materialId, @Param("quantity") BigDecimal quantity);

    /**
     * 扣减锁定库存（确认出库）
     */
    @Update("UPDATE wms_stock SET locked_quantity = locked_quantity - #{quantity}, " +
            "quantity = quantity - #{quantity}, " +
            "last_update_time = NOW(), version = version + 1 " +
            "WHERE material_id = #{materialId} AND locked_quantity >= #{quantity}")
    int deductLockedStock(@Param("materialId") Long materialId, @Param("quantity") BigDecimal quantity);
}
