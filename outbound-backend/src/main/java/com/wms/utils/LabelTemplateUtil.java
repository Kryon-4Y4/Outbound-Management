package com.wms.utils;

import com.alibaba.fastjson2.JSONObject;
import com.wms.entity.Material;
import com.wms.entity.Product;
import com.wms.entity.WmsOrder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 标签模板工具类
 * 支持ZPL（Zebra打印机）和PDF格式的标签生成
 */
@Component
public class LabelTemplateUtil {

    /**
     * 物料标签模板（ZPL格式）
     * 用于Zebra打印机
     */
    public String generateMaterialLabelZPL(Material material, BigDecimal quantity, String barcode) {
        StringBuilder zpl = new StringBuilder();
        zpl.append("^XA"); // 开始标签
        zpl.append("^CI28"); // UTF-8编码
        
        // 标题
        zpl.append("^FO20,20^A0N,40,40^FD物料标签^FS");
        
        // 物料编码
        zpl.append("^FO20,70^A0N,25,25^FD编码: ").append(material.getMaterialCode()).append("^FS");
        
        // 物料名称
        zpl.append("^FO20,105^A0N,25,25^FD名称: ").append(material.getMaterialName()).append("^FS");
        
        // 规格
        zpl.append("^FO20,140^A0N,20,20^FD规格: ").append(material.getSpecification()).append("^FS");
        
        // 数量
        zpl.append("^FO20,175^A0N,25,25^FD数量: ").append(quantity).append(" ").append(material.getUnit()).append("^FS");
        
        // 条码
        zpl.append("^FO20,220^BY3^BCN,80,Y,N,N^FD").append(barcode).append("^FS");
        
        // 日期
        zpl.append("^FO20,320^A0N,20,20^FD打印: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("^FS");
        
        zpl.append("^XZ"); // 结束标签
        return zpl.toString();
    }

    /**
     * 产品标签模板（ZPL格式）
     */
    public String generateProductLabelZPL(Product product, BigDecimal quantity, String serialNo) {
        StringBuilder zpl = new StringBuilder();
        zpl.append("^XA");
        zpl.append("^CI28");
        
        // 标题
        zpl.append("^FO20,20^A0N,40,40^FD产品标签^FS");
        
        // 条码
        zpl.append("^FO20,70^BY3^BCN,80,Y,N,N^FD").append(product.getProductBarcode()).append("^FS");
        
        // 产品名称
        zpl.append("^FO20,160^A0N,30,30^FD").append(product.getProductName()).append("^FS");
        
        // 品牌颜色
        zpl.append("^FO20,200^A0N,25,25^FD").append(product.getBrand()).append(" ").append(product.getColor()).append("^FS");
        
        // 规格
        zpl.append("^FO20,235^A0N,20,20^FD规格: ").append(product.getSpecification()).append("^FS");
        
        // 数量
        zpl.append("^FO20,265^A0N,25,25^FD数量: ").append(quantity).append(" ").append(product.getUnit()).append("^FS");
        
        // 序列号
        zpl.append("^FO20,300^A0N,20,20^FDSN: ").append(serialNo).append("^FS");
        
        zpl.append("^XZ");
        return zpl.toString();
    }

    /**
     * 外箱标签模板（ZPL格式）
     */
    public String generateBoxLabelZPL(WmsOrder order, int boxNo, int totalBoxes, String containerNo) {
        StringBuilder zpl = new StringBuilder();
        zpl.append("^XA");
        zpl.append("^CI28");
        
        // 标题
        zpl.append("^FO20,20^A0N,50,50^FD外箱标签^FS");
        
        // 货单号
        zpl.append("^FO20,80^A0N,30,30^FD货单: ").append(order.getOrderNo()).append("^FS");
        
        // 客户
        zpl.append("^FO20,120^A0N,25,25^FD客户: ").append(order.getCustomerName()).append("^FS");
        
        // 目的地
        zpl.append("^FO20,155^A0N,25,25^FD目的: ").append(order.getDestination()).append("^FS");
        
        // 箱号
        zpl.append("^FO20,190^A0N,35,35^FD箱号: ").append(boxNo).append(" / ").append(totalBoxes).append("^FS");
        
        // 集装箱号
        zpl.append("^FO20,235^A0N,25,25^FD柜号: ").append(containerNo).append("^FS");
        
        // 箱号条码
        String boxBarcode = order.getOrderNo() + "-" + String.format("%03d", boxNo);
        zpl.append("^FO20,275^BY3^BCN,80,Y,N,N^FD").append(boxBarcode).append("^FS");
        
        zpl.append("^XZ");
        return zpl.toString();
    }

    /**
     * 生成标签JSON数据（用于PDF生成或前端打印）
     */
    public JSONObject generateLabelJson(String labelType, Object data) {
        JSONObject label = new JSONObject();
        label.put("type", labelType);
        label.put("createTime", LocalDateTime.now().toString());
        label.put("data", data);
        return label;
    }

    /**
     * 获取标签模板配置
     */
    public JSONObject getLabelTemplateConfig(String templateCode) {
        JSONObject config = new JSONObject();
        
        switch (templateCode) {
            case "MATERIAL":
                config.put("width", 80);  // mm
                config.put("height", 60); // mm
                config.put("dpi", 300);
                config.put("orientation", "portrait");
                break;
            case "PRODUCT":
                config.put("width", 80);
                config.put("height", 100);
                config.put("dpi", 300);
                config.put("orientation", "portrait");
                break;
            case "BOX":
                config.put("width", 100);
                config.put("height", 150);
                config.put("dpi", 203);
                config.put("orientation", "portrait");
                break;
            default:
                config.put("width", 80);
                config.put("height", 60);
                config.put("dpi", 300);
        }
        
        return config;
    }
}
