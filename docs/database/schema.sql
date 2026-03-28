-- =====================================================
-- WMS仓库管理系统 数据库初始化脚本
-- 数据库: wms
-- 编码: utf8mb4
-- 创建日期: 2024-01-01
-- =====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS wms 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE wms;

-- =====================================================
-- 1. 系统管理表
-- =====================================================

-- 用户表
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(200) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志: 0-未删除 1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 角色表
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    description VARCHAR(200) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- 用户角色关联表
CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (role_id) REFERENCES sys_role(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 菜单表
CREATE TABLE sys_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '菜单ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    menu_type TINYINT COMMENT '菜单类型: 1-目录 2-菜单 3-按钮',
    icon VARCHAR(50) COMMENT '菜单图标',
    path VARCHAR(200) COMMENT '路由路径',
    component VARCHAR(200) COMMENT '组件路径',
    permission VARCHAR(100) COMMENT '权限标识',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统菜单表';

-- 角色菜单关联表
CREATE TABLE sys_role_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_menu (role_id, menu_id),
    FOREIGN KEY (role_id) REFERENCES sys_role(id),
    FOREIGN KEY (menu_id) REFERENCES sys_menu(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 操作日志表
CREATE TABLE sys_operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT COMMENT '操作用户ID',
    username VARCHAR(50) COMMENT '操作用户名',
    operation VARCHAR(100) COMMENT '操作描述',
    method VARCHAR(200) COMMENT '请求方法',
    params TEXT COMMENT '请求参数',
    ip VARCHAR(50) COMMENT 'IP地址',
    duration BIGINT COMMENT '执行时长(ms)',
    status TINYINT COMMENT '状态: 0-失败 1-成功',
    error_msg TEXT COMMENT '错误信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- =====================================================
-- 2. 物料管理表
-- =====================================================

-- 物料表
CREATE TABLE wms_material (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '物料ID',
    material_code VARCHAR(50) NOT NULL UNIQUE COMMENT '物料编码',
    material_name VARCHAR(100) NOT NULL COMMENT '物料名称',
    specification VARCHAR(200) COMMENT '规格型号',
    unit VARCHAR(20) COMMENT '计量单位',
    packing_location VARCHAR(100) COMMENT '打包位置',
    description TEXT COMMENT '物料描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_code (material_code),
    INDEX idx_name (material_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物料表';

-- =====================================================
-- 3. 产品管理表
-- =====================================================

-- 产品表
CREATE TABLE wms_product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '产品ID',
    product_barcode VARCHAR(50) NOT NULL UNIQUE COMMENT '产品条码',
    product_name VARCHAR(100) NOT NULL COMMENT '产品名称',
    unit VARCHAR(20) COMMENT '计量单位',
    brand VARCHAR(50) COMMENT '品牌',
    color VARCHAR(50) COMMENT '颜色',
    specification VARCHAR(200) COMMENT '规格',
    description TEXT COMMENT '产品描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_barcode (product_barcode),
    INDEX idx_name (product_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品表';

-- 产品物料关联表（BOM）
CREATE TABLE wms_product_material (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    material_id BIGINT NOT NULL COMMENT '物料ID',
    quantity DECIMAL(10, 2) NOT NULL DEFAULT 1 COMMENT '所需数量',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_product_material (product_id, material_id),
    FOREIGN KEY (product_id) REFERENCES wms_product(id),
    FOREIGN KEY (material_id) REFERENCES wms_material(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品物料关联表';

-- =====================================================
-- 4. 货单管理表
-- =====================================================

-- 货单表
CREATE TABLE wms_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '货单ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '货单编号',
    order_type TINYINT DEFAULT 1 COMMENT '货单类型: 1-进口 2-出口',
    customer_name VARCHAR(100) COMMENT '客户名称',
    customer_contact VARCHAR(50) COMMENT '客户联系人',
    customer_phone VARCHAR(20) COMMENT '客户电话',
    destination VARCHAR(200) COMMENT '目的地',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-待处理 1-处理中 2-已完成 3-已取消',
    remark TEXT COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_no (order_no),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='货单表';

-- 客柜表（集装箱）
CREATE TABLE wms_container (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '客柜ID',
    order_id BIGINT NOT NULL COMMENT '货单ID',
    container_no VARCHAR(50) NOT NULL COMMENT '集装箱号',
    container_type VARCHAR(20) COMMENT '箱型: 20GP/40GP/40HQ等',
    seal_no VARCHAR(50) COMMENT '封条号',
    max_weight DECIMAL(10, 2) COMMENT '最大载重(kg)',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-空箱 1-装载中 2-满载 3-已封箱',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_order_container (order_id, container_no),
    FOREIGN KEY (order_id) REFERENCES wms_order(id),
    INDEX idx_container_no (container_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客柜表';

-- 货单产品明细表
CREATE TABLE wms_order_product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    order_id BIGINT NOT NULL COMMENT '货单ID',
    container_id BIGINT COMMENT '客柜ID(可为空)',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    quantity INT NOT NULL DEFAULT 0 COMMENT '计划数量',
    packed_quantity INT DEFAULT 0 COMMENT '已打包数量',
    unit VARCHAR(20) COMMENT '单位',
    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (order_id) REFERENCES wms_order(id),
    FOREIGN KEY (container_id) REFERENCES wms_container(id),
    FOREIGN KEY (product_id) REFERENCES wms_product(id),
    INDEX idx_order (order_id),
    INDEX idx_container (container_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='货单产品明细表';

-- =====================================================
-- 5. 打包管理表
-- =====================================================

-- 打包记录表
CREATE TABLE wms_packing_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    order_id BIGINT NOT NULL COMMENT '货单ID',
    container_id BIGINT NOT NULL COMMENT '客柜ID',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    packing_type TINYINT DEFAULT 1 COMMENT '打包类型: 1-传送带 2-脚装',
    quantity INT NOT NULL DEFAULT 0 COMMENT '打包数量',
    label_printed TINYINT DEFAULT 0 COMMENT '标签是否打印: 0-否 1-是',
    packer_id BIGINT COMMENT '打包员ID',
    packer_name VARCHAR(50) COMMENT '打包员姓名',
    packing_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '打包时间',
    remark VARCHAR(200) COMMENT '备注',
    FOREIGN KEY (order_id) REFERENCES wms_order(id),
    FOREIGN KEY (container_id) REFERENCES wms_container(id),
    FOREIGN KEY (product_id) REFERENCES wms_product(id),
    INDEX idx_order (order_id),
    INDEX idx_container (container_id),
    INDEX idx_packing_time (packing_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打包记录表';

-- 标签打印记录表
CREATE TABLE wms_label_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    order_id BIGINT NOT NULL COMMENT '货单ID',
    label_type TINYINT COMMENT '标签类型: 1-物料标签 2-产品标签 3-箱标签',
    ref_id BIGINT COMMENT '关联ID',
    label_content TEXT COMMENT '标签内容(JSON)',
    print_count INT DEFAULT 1 COMMENT '打印份数',
    printer_name VARCHAR(50) COMMENT '打印机名称',
    printed_by BIGINT COMMENT '打印人ID',
    printed_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '打印时间',
    FOREIGN KEY (order_id) REFERENCES wms_order(id),
    INDEX idx_order (order_id),
    INDEX idx_printed_time (printed_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签打印记录表';

-- =====================================================
-- 6. 数据字典表
-- =====================================================

-- 字典类型表
CREATE TABLE sys_dict_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '字典ID',
    dict_code VARCHAR(50) NOT NULL UNIQUE COMMENT '字典编码',
    dict_name VARCHAR(100) NOT NULL COMMENT '字典名称',
    description VARCHAR(200) COMMENT '字典描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_dict_code (dict_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- 字典数据表
CREATE TABLE sys_dict_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '字典数据ID',
    dict_code VARCHAR(50) NOT NULL COMMENT '字典编码',
    dict_label VARCHAR(100) NOT NULL COMMENT '字典标签',
    dict_value VARCHAR(100) NOT NULL COMMENT '字典键值',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    remark VARCHAR(200) COMMENT '备注',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (dict_code) REFERENCES sys_dict_type(dict_code),
    INDEX idx_dict_code (dict_code),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- =====================================================
-- 7. 定时任务表
-- =====================================================

-- 定时任务配置表
CREATE TABLE sys_job (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任务ID',
    job_name VARCHAR(100) NOT NULL COMMENT '任务名称',
    job_group VARCHAR(50) DEFAULT 'DEFAULT' COMMENT '任务组名',
    job_class VARCHAR(200) NOT NULL COMMENT '执行类',
    job_method VARCHAR(100) NOT NULL COMMENT '执行方法',
    job_params TEXT COMMENT '参数',
    cron_expression VARCHAR(50) NOT NULL COMMENT 'cron表达式',
    job_status TINYINT DEFAULT 0 COMMENT '状态: 0-正常 1-暂停',
    description VARCHAR(200) COMMENT '任务描述',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_job_group (job_group),
    INDEX idx_job_status (job_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务配置表';

-- 定时任务日志表
CREATE TABLE sys_job_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    job_id BIGINT NOT NULL COMMENT '任务ID',
    job_name VARCHAR(100) COMMENT '任务名称',
    job_group VARCHAR(50) COMMENT '任务组名',
    job_class VARCHAR(200) COMMENT '执行类',
    job_method VARCHAR(100) COMMENT '执行方法',
    job_params TEXT COMMENT '参数',
    execute_time DATETIME COMMENT '执行时间',
    execute_result TINYINT COMMENT '执行结果: 0-失败 1-成功',
    execute_message TEXT COMMENT '执行消息',
    execute_duration BIGINT COMMENT '执行时长(ms)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (job_id) REFERENCES sys_job(id),
    INDEX idx_job_id (job_id),
    INDEX idx_execute_time (execute_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务日志表';

-- =====================================================
-- 8. 文件上传记录表
-- =====================================================

CREATE TABLE sys_file_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文件ID',
    file_name VARCHAR(200) NOT NULL COMMENT '文件名称',
    original_name VARCHAR(200) COMMENT '原始文件名',
    file_suffix VARCHAR(20) COMMENT '文件后缀',
    file_url VARCHAR(500) NOT NULL COMMENT '文件URL',
    file_size BIGINT COMMENT '文件大小(字节)',
    file_type VARCHAR(50) COMMENT '文件MIME类型',
    storage_type TINYINT DEFAULT 1 COMMENT '存储类型: 1-本地 2-MinIO 3-阿里云OSS 4-腾讯云COS',
    module VARCHAR(50) COMMENT '业务模块',
    business_id BIGINT COMMENT '业务ID',
    upload_by BIGINT COMMENT '上传人',
    upload_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    INDEX idx_module (module),
    INDEX idx_business_id (business_id),
    INDEX idx_upload_time (upload_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件上传记录表';

-- =====================================================
-- 9. 数据库索引优化（补充）
-- =====================================================

-- 物料表索引优化
CREATE INDEX idx_material_status ON wms_material(status, deleted);
CREATE INDEX idx_material_create_time ON wms_material(create_time);

-- 产品表索引优化
CREATE INDEX idx_product_status ON wms_product(status, deleted);
CREATE INDEX idx_product_barcode ON wms_product(product_barcode);

-- 产品物料关联表索引
CREATE INDEX idx_product_material_product ON wms_product_material(product_id);
CREATE INDEX idx_product_material_material ON wms_product_material(material_id);

-- 货单表索引优化
CREATE INDEX idx_order_status ON wms_order(status, deleted);
CREATE INDEX idx_order_customer ON wms_order(customer_name);
CREATE INDEX idx_order_time ON wms_order(create_time);

-- 货单产品明细表索引
CREATE INDEX idx_order_product_order ON wms_order_product(order_id);
CREATE INDEX idx_order_product_container ON wms_order_product(container_id);
CREATE INDEX idx_order_product_product ON wms_order_product(product_id);

-- 客柜表索引
CREATE INDEX idx_container_order ON wms_container(order_id);
CREATE INDEX idx_container_status ON wms_container(status);

-- 打包记录表索引优化
CREATE INDEX idx_packing_order ON wms_packing_record(order_id);
CREATE INDEX idx_packing_container ON wms_packing_record(container_id);
CREATE INDEX idx_packing_product ON wms_packing_record(product_id);
CREATE INDEX idx_packing_time ON wms_packing_record(packing_time);

-- 用户表索引优化
CREATE INDEX idx_user_status ON sys_user(status, deleted);
CREATE INDEX idx_user_create_time ON sys_user(create_time);

-- 操作日志表索引优化
CREATE INDEX idx_log_user ON sys_operation_log(user_id);
CREATE INDEX idx_log_time ON sys_operation_log(create_time);

-- 库存表索引
CREATE INDEX idx_stock_material ON wms_stock(material_id);
CREATE INDEX idx_stock_available ON wms_stock(available_quantity);

-- 库存变动记录表索引
CREATE INDEX idx_stock_record_material ON wms_stock_record(material_id);
CREATE INDEX idx_stock_record_type ON wms_stock_record(change_type);
CREATE INDEX idx_stock_record_time ON wms_stock_record(create_time);

-- =====================================================
-- 6. 库存管理表
-- =====================================================

-- 库存表
CREATE TABLE wms_stock (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '库存ID',
    material_id BIGINT NOT NULL COMMENT '物料ID',
    quantity DECIMAL(10, 2) DEFAULT 0 COMMENT '当前库存数量',
    available_quantity DECIMAL(10, 2) DEFAULT 0 COMMENT '可用库存数量',
    locked_quantity DECIMAL(10, 2) DEFAULT 0 COMMENT '锁定库存数量',
    warning_threshold DECIMAL(10, 2) DEFAULT 100 COMMENT '预警库存阈值',
    warehouse_location VARCHAR(100) COMMENT '仓库位置',
    last_update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    version INT DEFAULT 0 COMMENT '版本号（乐观锁）',
    UNIQUE KEY uk_material (material_id),
    INDEX idx_warning (available_quantity, warning_threshold)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- 库存变动记录表
CREATE TABLE wms_stock_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    material_id BIGINT NOT NULL COMMENT '物料ID',
    change_type TINYINT COMMENT '变动类型: 1-入库 2-出库 3-锁定 4-解锁 5-盘点调整',
    change_quantity DECIMAL(10, 2) COMMENT '变动数量（正数增加，负数减少）',
    before_quantity DECIMAL(10, 2) COMMENT '变动前数量',
    after_quantity DECIMAL(10, 2) COMMENT '变动后数量',
    biz_type VARCHAR(20) COMMENT '关联业务类型: ORDER-货单 PACKING-打包',
    biz_id BIGINT COMMENT '关联业务ID',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (material_id) REFERENCES wms_material(id),
    INDEX idx_material (material_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存变动记录表';
