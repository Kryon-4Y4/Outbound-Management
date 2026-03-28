package com.wms.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

/**
 * MyBatis Plus 代码生成器
 * 一键生成 Controller、Service、Mapper、Entity
 */
public class CodeGenerator {

    /**
     * 数据库连接信息
     */
    private static final String DB_URL = "jdbc:mysql://localhost:3306/wms?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "123456";

    /**
     * 作者名
     */
    private static final String AUTHOR = "WMS Team";

    /**
     * 父包名
     */
    private static final String PARENT_PACKAGE = "com.wms";

    /**
     * 输出目录（相对于项目根目录）
     */
    private static final String OUTPUT_DIR = System.getProperty("user.dir") + "/wms-backend/src/main";

    public static void main(String[] args) {
        // 要生成的表名，多个用逗号分隔
        String tables = "wms_material,wms_product,wms_order";
        
        generate(tables);
    }

    /**
     * 生成代码
     */
    public static void generate(String tables) {
        FastAutoGenerator.create(DB_URL, DB_USERNAME, DB_PASSWORD)
                // 全局配置
                .globalConfig(builder -> builder
                        .author(AUTHOR)
                        .outputDir(OUTPUT_DIR + "/java")
                        .disableOpenDir()
                        .enableSwagger()
                )
                // 包配置
                .packageConfig(builder -> builder
                        .parent(PARENT_PACKAGE)
                        .entity("entity")
                        .service("service")
                        .serviceImpl("service.impl")
                        .mapper("mapper")
                        .xml("mapper")
                        .controller("controller")
                        .pathInfo(Collections.singletonMap(
                                OutputFile.xml, 
                                OUTPUT_DIR + "/resources/mapper"
                        ))
                )
                // 策略配置
                .strategyConfig(builder -> builder
                        .addInclude(tables.split(","))
                        .addTablePrefix("wms_", "sys_")
                        // Entity策略
                        .entityBuilder()
                        .enableLombok()
                        .enableChainModel()
                        .naming(NamingStrategy.underline_to_camel)
                        .columnNaming(NamingStrategy.underline_to_camel)
                        .enableTableFieldAnnotation()
                        .enableActiveRecord()
                        // Controller策略
                        .controllerBuilder()
                        .enableRestStyle()
                        // Service策略
                        .serviceBuilder()
                        .formatServiceFileName("%sService")
                        .formatServiceImplFileName("%sServiceImpl")
                        // Mapper策略
                        .mapperBuilder()
                        .enableMapperAnnotation()
                        .enableBaseResultMap()
                        .enableBaseColumnList()
                )
                // 模板引擎
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
        
        System.out.println("代码生成完成！");
        System.out.println("输出目录: " + OUTPUT_DIR);
    }
}
