# WMS API 接口文档

## 基础信息

- **Base URL**: `http://localhost:8080/api`
- **Content-Type**: `application/json`
- **认证方式**: JWT Token (Header: `Authorization: Bearer {token}`)

---

## 1. 认证模块

### 1.1 用户登录

**POST** `/auth/login`

#### 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |

#### 请求示例

```json
{
  "username": "admin",
  "password": "admin123"
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "userInfo": {
      "id": 1,
      "username": "admin",
      "realName": "系统管理员",
      "avatar": ""
    }
  }
}
```

### 1.2 用户登出

**POST** `/auth/logout`

#### 响应示例

```json
{
  "code": 200,
  "message": "登出成功",
  "data": null
}
```

---

## 2. 用户管理

### 2.1 获取用户列表

**GET** `/user/list`

#### 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页数量，默认20 |
| keyword | string | 否 | 搜索关键词 |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 100,
    "list": [
      {
        "id": 1,
        "username": "admin",
        "realName": "系统管理员",
        "phone": "13800138000",
        "status": 1,
        "createTime": "2024-01-01 10:00:00"
      }
    ]
  }
}
```

### 2.2 新增用户

**POST** `/user/add`

#### 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |
| realName | string | 否 | 真实姓名 |
| phone | string | 否 | 手机号 |
| email | string | 否 | 邮箱 |
| status | int | 否 | 状态 0-禁用 1-启用 |

---

## 3. 物料管理

### 3.1 获取物料列表

**GET** `/material/list`

#### 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码 |
| size | int | 否 | 每页数量 |
| keyword | string | 否 | 物料编码/名称搜索 |
| status | int | 否 | 状态筛选 |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 50,
    "list": [
      {
        "id": 1,
        "materialCode": "MAT001",
        "materialName": "纸箱-A型",
        "specification": "50x40x30cm",
        "unit": "个",
        "packingLocation": "A区-01货架",
        "status": 1
      }
    ]
  }
}
```

### 3.2 新增物料

**POST** `/material/add`

#### 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| materialCode | string | 是 | 物料编码 |
| materialName | string | 是 | 物料名称 |
| specification | string | 否 | 规格型号 |
| unit | string | 否 | 计量单位 |
| packingLocation | string | 否 | 打包位置 |
| description | string | 否 | 描述 |

### 3.3 导入物料

**POST** `/material/import`

#### 请求参数

- Content-Type: `multipart/form-data`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | file | 是 | Excel文件(.xlsx) |

#### 响应示例

```json
{
  "code": 200,
  "message": "导入成功",
  "data": {
    "total": 100,
    "success": 98,
    "fail": 2,
    "failList": [
      {"row": 5, "reason": "物料编码重复"}
    ]
  }
}
```

---

## 4. 产品管理

### 4.1 获取产品列表

**GET** `/product/list`

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 30,
    "list": [
      {
        "id": 1,
        "productBarcode": "PRD001",
        "productName": "智能手表-黑色",
        "brand": "TechBrand",
        "color": "黑色",
        "unit": "个",
        "status": 1
      }
    ]
  }
}
```

### 4.2 获取产品物料清单(BOM)

**GET** `/product/bom/{productId}`

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "materialId": 1,
      "materialCode": "MAT001",
      "materialName": "纸箱-A型",
      "quantity": 1
    },
    {
      "materialId": 3,
      "materialCode": "MAT003",
      "materialName": "泡沫填充物",
      "quantity": 2
    }
  ]
}
```

---

## 5. 货单管理

### 5.1 获取货单列表

**GET** `/order/list`

#### 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码 |
| size | int | 否 | 每页数量 |
| orderNo | string | 否 | 货单编号 |
| customerName | string | 否 | 客户名称 |
| status | int | 否 | 状态 |
| startDate | string | 否 | 开始日期 |
| endDate | string | 否 | 结束日期 |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 20,
    "list": [
      {
        "id": 1,
        "orderNo": "ORD20240101001",
        "customerName": "ABC国际贸易公司",
        "customerContact": "张三",
        "destination": "美国洛杉矶",
        "status": 0,
        "createTime": "2024-01-01 10:00:00"
      }
    ]
  }
}
```

### 5.2 获取货单详情

**GET** `/order/detail/{orderId}`

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "orderNo": "ORD20240101001",
    "customerName": "ABC国际贸易公司",
    "containers": [
      {
        "id": 1,
        "containerNo": "MSCU1234567",
        "containerType": "40HQ",
        "products": [
          {
            "productId": 1,
            "productName": "智能手表-黑色",
            "quantity": 500,
            "packedQuantity": 0
          }
        ]
      }
    ]
  }
}
```

### 5.3 货单导入

**POST** `/order/import`

支持导入货单基础信息和产品明细。

---

## 6. 标签打印

### 6.1 打印货单标签

**POST** `/label/print`

#### 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| orderId | long | 是 | 货单ID |
| containerId | long | 否 | 客柜ID(可选) |
| labelType | int | 是 | 标签类型 1-物料 2-产品 3-箱标签 |
| printCount | int | 否 | 打印份数，默认1 |

### 6.2 获取标签模板

**GET** `/label/template/{labelType}`

---

## 7. 打包作业

### 7.1 扫描产品

**POST** `/packing/scan`

#### 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| orderId | long | 是 | 货单ID |
| containerId | long | 是 | 客柜ID |
| barcode | string | 是 | 产品条码 |
| packingType | int | 是 | 打包类型 1-传送带 2-脚装 |

#### 响应示例

```json
{
  "code": 200,
  "message": "扫描成功",
  "data": {
    "productId": 1,
    "productName": "智能手表-黑色",
    "productBarcode": "PRD001",
    "currentQuantity": 1,
    "totalQuantity": 500,
    "remainQuantity": 499
  }
}
```

### 7.2 确认打包

**POST** `/packing/confirm`

#### 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| orderId | long | 是 | 货单ID |
| containerId | long | 是 | 客柜ID |
| productId | long | 是 | 产品ID |
| quantity | int | 是 | 打包数量 |
| packingType | int | 是 | 打包类型 |

### 7.3 获取打包记录

**GET** `/packing/record/list`

#### 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| orderId | long | 否 | 货单ID |
| containerId | long | 否 | 客柜ID |
| page | int | 否 | 页码 |
| size | int | 否 | 每页数量 |

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未授权/Token过期 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
| 1001 | 用户名或密码错误 |
| 1002 | 账号已被禁用 |
| 1003 | 验证码错误 |
| 2001 | 物料编码已存在 |
| 2002 | 产品条码已存在 |
| 3001 | 货单编号已存在 |
| 3002 | 集装箱号已存在 |
| 4001 | 产品未在货单中 |
| 4002 | 打包数量超出计划 |

---

## 附录：状态定义

### 货单状态

| 值 | 含义 |
|----|------|
| 0 | 待处理 |
| 1 | 处理中 |
| 2 | 已完成 |
| 3 | 已取消 |

### 客柜状态

| 值 | 含义 |
|----|------|
| 0 | 空箱 |
| 1 | 装载中 |
| 2 | 满载 |
| 3 | 已封箱 |

### 打包类型

| 值 | 含义 |
|----|------|
| 1 | 传送带打包 |
| 2 | 脚装打包 |
