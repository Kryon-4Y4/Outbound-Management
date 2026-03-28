-- =====================================================
-- WMS仓库管理系统 初始化数据
-- =====================================================

USE wms;

-- =====================================================
-- 1. 初始化角色数据
-- =====================================================
INSERT INTO sys_role (role_code, role_name, description, status) VALUES
('admin', '系统管理员', '拥有系统所有权限', 1),
('operator', '操作员', '日常操作权限', 1),
('packer', '打包员', '打包作业权限', 1),
('viewer', '查看员', '只读权限', 1);

-- =====================================================
-- 2. 初始化管理员用户
-- 密码: admin123 (BCrypt加密)
-- =====================================================
INSERT INTO sys_user (username, password, real_name, phone, email, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '系统管理员', '13800138000', 'admin@wms.com', 1),
('operator', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '操作员', '13800138001', 'operator@wms.com', 1),
('packer', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '打包员', '13800138002', 'packer@wms.com', 1);

-- =====================================================
-- 3. 用户角色关联
-- =====================================================
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),  -- admin -> 系统管理员
(2, 2),  -- operator -> 操作员
(3, 3);  -- packer -> 打包员

-- =====================================================
-- 4. 初始化菜单数据
-- =====================================================
INSERT INTO sys_menu (parent_id, menu_name, menu_type, icon, path, component, permission, sort_order) VALUES
-- 首页
(0, '首页', 1, 'HomeFilled', '/dashboard', 'system/dashboard', 'dashboard', 1),

-- 用户管理
(0, '用户管理', 1, 'UserFilled', '/user', NULL, 'user', 2),
(0, '用户列表', 2, NULL, '/user', 'user/index', 'user:list', 1),

-- 物料管理
(0, '物料管理', 1, 'Box', '/material', NULL, 'material', 3),
(0, '物料列表', 2, NULL, '/material', 'material/index', 'material:list', 1),

-- 产品管理
(0, '产品管理', 1, 'GoodsFilled', '/product', NULL, 'product', 4),
(0, '产品列表', 2, NULL, '/product', 'product/index', 'product:list', 1),

-- 货单管理
(0, '货单管理', 1, 'Document', '/order', NULL, 'order', 5),
(0, '货单列表', 2, NULL, '/order', 'order/index', 'order:list', 1),
(0, '客柜管理', 2, NULL, '/order/container', 'order/container', 'order:container', 2),
(0, '标签打印', 2, NULL, '/order/label', 'order/label', 'order:label', 3),
(0, '打包记录', 2, NULL, '/order/record', 'order/record', 'order:record', 4),

-- 产品打包
(0, '产品打包', 1, 'ShoppingCartFull', '/packing', NULL, 'packing', 6),
(0, '传送带打包', 2, NULL, '/packing/conveyor', 'packing/conveyor', 'packing:conveyor', 1),
(0, '脚装打包', 2, NULL, '/packing/manual', 'packing/manual', 'packing:manual', 2);

-- =====================================================
-- 5. 初始化物料数据
-- =====================================================
INSERT INTO wms_material (material_code, material_name, specification, unit, packing_location, description, status, create_by) VALUES
('MAT001', '纸箱-A型', '50x40x30cm', '个', 'A区-01货架', '标准纸箱A型', 1, 1),
('MAT002', '纸箱-B型', '60x50x40cm', '个', 'A区-02货架', '标准纸箱B型', 1, 1),
('MAT003', '泡沫填充物', '标准规格', '包', 'B区-01货架', '防震泡沫填充物', 1, 1),
('MAT004', '胶带', '透明宽胶带', '卷', 'B区-02货架', '封箱胶带', 1, 1),
('MAT005', '标签纸', '100x50mm', '卷', 'B区-03货架', '产品标签纸', 1, 1),
('MAT006', '包装袋', '大号', '个', 'A区-03货架', '防潮包装袋', 1, 1);

-- =====================================================
-- 6. 初始化产品数据
-- =====================================================
INSERT INTO wms_product (product_barcode, product_name, unit, brand, color, specification, description, status, create_by) VALUES
('PRD001', '智能手表-黑色', '个', 'TechBrand', '黑色', '标准版', '智能穿戴设备', 1, 1),
('PRD002', '智能手表-白色', '个', 'TechBrand', '白色', '标准版', '智能穿戴设备', 1, 1),
('PRD003', '蓝牙耳机-黑色', '个', 'AudioPro', '黑色', '降噪版', '无线蓝牙耳机', 1, 1),
('PRD004', '蓝牙耳机-白色', '个', 'AudioPro', '白色', '降噪版', '无线蓝牙耳机', 1, 1),
('PRD005', '移动电源', '个', 'PowerBank', '银色', '20000mAh', '大容量移动电源', 1, 1);

-- =====================================================
-- 7. 产品物料BOM数据
-- =====================================================
INSERT INTO wms_product_material (product_id, material_id, quantity, sort_order) VALUES
-- 智能手表-黑色
(1, 1, 1, 1),  -- 纸箱-A型 x1
(1, 3, 2, 2),  -- 泡沫填充物 x2
(1, 4, 1, 3),  -- 胶带 x1
(1, 5, 1, 4),  -- 标签纸 x1

-- 智能手表-白色
(2, 1, 1, 1),
(2, 3, 2, 2),
(2, 4, 1, 3),
(2, 5, 1, 4),

-- 蓝牙耳机-黑色
(3, 1, 1, 1),
(3, 6, 1, 2),
(3, 4, 1, 3),
(3, 5, 1, 4),

-- 蓝牙耳机-白色
(4, 1, 1, 1),
(4, 6, 1, 2),
(4, 4, 1, 3),
(4, 5, 1, 4),

-- 移动电源
(5, 2, 1, 1),  -- 纸箱-B型 x1
(5, 3, 3, 2),  -- 泡沫填充物 x3
(5, 4, 2, 3),  -- 胶带 x2
(5, 5, 2, 4);  -- 标签纸 x2

-- =====================================================
-- 8. 初始化货单数据
-- =====================================================
INSERT INTO wms_order (order_no, order_type, customer_name, customer_contact, customer_phone, destination, status, remark, create_by) VALUES
('ORD20240101001', 1, 'ABC国际贸易公司', '张三', '13900139000', '美国洛杉矶', 0, '首批试订单', 1),
('ORD20240101002', 1, 'XYZ实业有限公司', '李四', '13900139001', '德国汉堡', 1, '常规订单', 1),
('ORD20240101003', 2, 'MNO进出口公司', '王五', '13900139002', '日本东京', 2, '已完成发货', 1);

-- =====================================================
-- 9. 初始化客柜数据
-- =====================================================
INSERT INTO wms_container (order_id, container_no, container_type, seal_no, max_weight, status) VALUES
(2, 'MSCU1234567', '40HQ', 'SL001', 25000.00, 1),
(2, 'MSCU7654321', '40HQ', 'SL002', 25000.00, 0);

-- =====================================================
-- 10. 初始化货单产品明细
-- =====================================================
INSERT INTO wms_order_product (order_id, container_id, product_id, quantity, packed_quantity, unit, remark) VALUES
-- 货单2的产品
(2, 1, 1, 500, 0, '个', '智能手表-黑色'),
(2, 1, 3, 300, 0, '个', '蓝牙耳机-黑色'),
(2, 2, 5, 200, 0, '个', '移动电源'),

-- 货单3的产品(已完成)
(3, NULL, 2, 1000, 1000, '个', '智能手表-白色'),
(3, NULL, 4, 800, 800, '个', '蓝牙耳机-白色');
