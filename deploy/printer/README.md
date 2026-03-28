# 打印机配置说明

## 目录结构

```
deploy/printer/
├── zebra/                    # Zebra斑马打印机配置
│   ├── zebra-config.prn      # 基础配置文件
│   ├── fonts/                # 字体文件
│   └── templates/            # 标签模板
├── tsc/                      # TSC打印机配置
├── godex/                    # Godex打印机配置
└── drivers/                  # 打印机驱动
    ├── windows/              # Windows驱动
    ├── linux/                # Linux驱动
    └── macos/                # macOS驱动
```

## 快速开始

### 1. Zebra斑马打印机

#### Windows系统

1. 安装驱动：运行 `drivers/windows/ZebraSetupUtilities.exe`
2. 配置打印机：双击 `zebra/zebra-config.prn` 发送至打印机
3. 测试打印：打开 `templates/test-label.zpl` 并打印

#### Linux系统

```bash
# 安装CUPS和Zebra驱动
sudo apt-get install cups
sudo apt-get install printer-driver-zpl

# 添加打印机
lpadmin -p ZebraPrinter -E -v socket://192.168.1.100:9100 -m everywhere

# 测试打印
cat zebra-config.prn | lp -d ZebraPrinter
```

### 2. 网络打印机配置

```javascript
// 前端配置（config/printer.js）
export const PRINTER_CONFIG = {
    // Zebra网络打印机
    ZEBRA_001: {
        type: 'zebra',
        connection: 'network',
        ip: '192.168.1.101',
        port: 9100,
        dpi: 203,
        width: 80,      // mm
        height: 60,     // mm
    },
    
    // USB打印机
    ZEBRA_002: {
        type: 'zebra',
        connection: 'usb',
        vid: '0x0a5f',
        pid: '0x0085',
        dpi: 203,
        width: 100,
        height: 150,
    },
    
    // 云打印机
    CLOUD_001: {
        type: 'cloud',
        provider: 'feie',
        sn: 'XXXXXXXXXXXX',
        key: 'XXXXXXXXXX',
    }
};
```

### 3. 字体安装

Zebra打印机需要安装中文字体才能正确打印中文：

```bash
# 下载中文字体
wget http://your-server/fonts/simsun.fnt -O zebra/fonts/simsun.fnt

# 通过ZebraNet Bridge安装
# 或使用Zebra Setup Utilities工具安装
```

## 故障排查

### 打印偏移

1. 执行校准命令：`~JC`
2. 检查标签传感器清洁
3. 调整标签间隙传感器位置

### 打印模糊

1. 清洁打印头
2. 调整打印浓度（ darkness ）
3. 检查碳带/标签纸质量

### 连接问题

```bash
# 测试网络连接
ping 192.168.1.100

# 测试端口
nc -zv 192.168.1.100 9100

# 查看打印机状态
lpstat -p ZebraPrinter
```

## 参考链接

- [Zebra ZPL II编程指南](https://www.zebra.com/content/dam/zebra_new_ia/en-us/manuals/printers/zpl-zbi2-pm-en.pdf)
- [TSC指令集](https://www.tscprinters.com/en/support/downloads)
- [CUPS文档](https://www.cups.org/doc/)
