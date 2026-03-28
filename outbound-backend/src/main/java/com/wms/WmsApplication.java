package com.wms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * WMS仓库管理系统启动类
 * 
 * @author WMS Team
 * @version 1.0.0
 */
@SpringBootApplication
@MapperScan("com.wms.mapper")
public class WmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(WmsApplication.class, args);
        System.out.println("========================================");
        System.out.println("  WMS仓库管理系统启动成功!");
        System.out.println("  访问地址: http://localhost:8080/api");
        System.out.println("========================================");
    }
}
