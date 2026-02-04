# 便利店库存管理系统｜完整版最终文档（Vue3 + Element Plus + SpringBoot2 + Security+JWT + 三层架构）

> **用途**：本文件作为 **唯一需求与实现规范**（放仓库给 CodeX/Codex 查询使用）。  
> **强制要求**：前端所有数据操作必须调用后端 API；后端严格三层架构；Entity/DTO/VO 分离；权限“前端不展示 + 后端强校验”；后端端口 **8080**；初始化 admin / 角色 / 权限数据。

---

## 目录
1. [项目目标与总原则](#0-项目目标与总原则)  
2. [技术栈与工程规范](#1-技术栈与工程规范)  
3. [权限模型](#2-权限模型必须)  
4. [功能需求（全量）](#3-功能需求全量)  
5. [前端路由与 UI 约束](#4-前端页面与路由vue3--element-plus)  
6. [后端安全（Spring Security + JWT）](#5-后端安全设计spring-security--jwt)  
7. [数据库设计（MySQL）](#6-数据库设计mysql)  
8. [API 契约（前后端对接）](#7-api-契约前后端对接)  
9. [后端三层架构规范（强制）](#8-后端强制架构规范)  
10. [初始化数据（admin/角色/权限）](#9-初始化数据admin角色权限)  
11. [前端对接配置（Vite Proxy）](#10-前端对接配置vite-proxy)  
12. [验收标准（必须通过）](#11-验收标准必须通过)  
13. [CodeX 任务拆分建议](#12-codex-任务拆分建议)  

---

## 0. 项目目标与总原则

### 0.1 目标
实现一个便利店库存管理系统（Web），包含：
- 登录：用户名 + 密码
- 首页：今日/月营业额 + **扫码出库购物车**
- 库存管理：查询库存，修改库存与售价
- 入库：扫码入库，**入库时必须设置进价与售价（不允许为空且>0）**
- 账目管理：日期区间销售额与利润统计，分页展示出库明细
- 用户管理：用户增删改查、禁用/启用、重置密码
- 角色管理：角色与权限点配置

### 0.2 总原则（强制）
1. **前端所有数据操作必须对接后端接口**（禁止最终依赖前端假数据/本地存储作为业务数据源）。  
2. 权限：前端无权限不展示模块/按钮；后端接口必须强校验（403）。  
3. 首页出库：**全局扫码可用**、多商品列表、展示库存、扫码同商品数量+1、可鼠标删行/改数量、总价汇总。  
4. 入库：必须填写进价与售价（均 > 0，不允许为空），且会更新商品当前默认价格。  
5. 后端架构：必须严格三层（Controller/Service/DAO），并采用 Entity/DTO/VO 分离。  
6. 利润准确：出库明细必须保存出库时成本价/售价，避免改价影响历史利润。  
7. 后端端口固定：**8080**。前端统一通过 `/api` 访问并代理至 `http://localhost:8080`。

---

## 1. 技术栈与工程规范

### 1.1 前端（必须）
- Vue3 + TypeScript + Vite
- UI：Element Plus
- Router：Vue Router
- 状态：Pinia
- 请求：Axios（统一拦截器：自动携带 JWT，处理 401/403）
- 规范：ESLint + Prettier
- UI 风格：简洁、清晰、收银效率优先（表格 + 卡片 + 固定汇总条）

### 1.2 后端（必须）
- Spring Boot 2.x + Java 8
- MySQL 8.x
- MyBatis-Plus
- 权限鉴权：Spring Security + JWT
- 参数校验：javax.validation
- 事务：出库、入库、库存调整必须事务（Service 层）

---

## 2. 权限模型（必须）

### 2.1 权限点（permission_code）
- STOCK_IN：入库
- STOCK_OUT：出库/销售（首页扫码模块）
- STOCK_EDIT：库存调整、售价修改
- ACCOUNT_VIEW：账目管理
- USER_MGR：用户管理
- ROLE_MGR：角色管理

### 2.2 权限规则
- **前端**：无权限 → 不展示菜单入口与按钮；路由守卫无权限跳 /403  
- **后端**：除登录外所有接口需 JWT 登录态；关键接口需权限点校验（@PreAuthorize）；无权限 403  

---

## 3. 功能需求（全量）

### 3.1 登录
- 用户名 + 密码登录
- 登录成功返回：token + user + permissions
- token 过期/无效：后端 401；前端清 token 并跳 /login

### 3.2 首页（营业额 + 全局扫码出库购物车）

#### 3.2.1 顶部营业额卡片
- 今日营业额 todayRevenue
- 当月营业额 monthRevenue

#### 3.2.2 下方扫码出库区域（高度约占 1/3）
> 购物车模式：支持多商品、多行、总价；全局扫码；展示库存；同条码再扫 qty+1。  
> 权限：STOCK_OUT（无则隐藏出库模块，可仅展示营业额卡片）。

**A) 全局扫码规则（首页任何时候都能扫）**
- 在首页路由下，无论鼠标点在哪（表格、按钮、空白），扫码枪一扫码即生效。
- 推荐实现：一个扫码 input（可隐藏/可见）作为扫码入口：
  - 页面任意点击 → 自动 focus 回扫码 input
  - 扫码输入以 Enter 结尾触发处理
- 当用户正在编辑数量输入框时：允许键盘输入数量，不误触扫码逻辑（最稳：仅当扫码 input 聚焦时处理扫码）

**B) 扫码行为**
- Enter 后调用：`GET /api/products/by-barcode?barcode=xxx`
- 商品不存在：提示“商品不存在，请先入库/新增”
- 商品存在：
  - 列表不存在该条码：新增行 qty=1
  - 列表已存在该条码：qty += 1
- 清空扫码输入并保持 focus

**C) 出库列表（多商品表格）每行字段**
- 商品名 name
- 条码 barcode
- 售价 salePrice（确认前展示）
- **库存 stock（必须展示）**
- 数量 qty（默认1，可 +/- 或输入）
- 小计 lineAmount = qty * salePrice
- 删除行按钮

**库存校验**
- 若任何行 qty > stock：该行提示库存不足；**禁用“确认出库”按钮**

**D) 汇总与提交**
- 必须展示总价：`totalAmount = Σ(qty * salePrice)`
- 建议展示总件数：`totalQty = Σ(qty)`
- 操作按钮：清空列表、确认出库
- 出库成功：清空购物车并刷新营业额

### 3.3 库存管理
- 商品分页查询（keyword：条码/商品名）
- 展示：条码、商品名、分类、库存、进价、售价、状态
- 修改（需 STOCK_EDIT）：
  - 售价修改
  - 库存调整（建议记录调整原因）
- 无 STOCK_EDIT：不展示修改按钮/入口

### 3.4 入库（扫码入库 + 必须设置进价与售价，不允许为空且>0）
扫码条码后：

**A) 商品已存在**
- 展示商品信息（名称、条码、当前库存）
- 必填：
  - qty 入库数量（>=1）
  - costPrice 进价（>0）
  - salePrice 售价（>0）
- 确认入库（事务）：
  1) product.stock += qty  
  2) 更新商品当前默认价格：product.cost_price = costPrice；product.sale_price = salePrice  
  3) 写入入库记录与明细（明细保存 costPrice、salePrice）

**B) 商品不存在**
- 弹出“新增商品 + 入库”对话框
- 必填：
  - barcode（扫码得到）
  - name
  - category（建议必填）
  - qty（>=1）
  - costPrice（>0）
  - salePrice（>0）
- 提交（事务）：先新增商品（stock=0），再入库并写入入库单/明细

### 3.5 账目管理（ACCOUNT_VIEW）
- 输入开始日期、结束日期
- 展示：区间销售额、区间利润
- 分页展示出库明细：时间、单号、商品、数量、售价、成本、利润、操作员等
- 利润计算依据：出库明细保存的 sale_price 与 cost_price

### 3.6 用户管理（USER_MGR）
- 用户分页增删改查
- 启用/禁用
- 重置密码

### 3.7 角色管理（ROLE_MGR）
- 角色 CRUD
- 配置角色权限点（至少包含 2.1 全部）
- 权限变更后重新登录或刷新 /me 生效

---

## 4. 前端页面与路由（Vue3 + Element Plus）

### 4.1 路由
- /login
- /（Layout）
  - /home 首页
  - /inventory 库存管理
  - /stock-in 入库
  - /accounts 账目管理
  - /users 用户管理
  - /roles 角色管理
  - /403 无权限页

### 4.2 菜单显示映射（按 permissions）
- 首页出库模块：STOCK_OUT（无则隐藏）
- 入库：STOCK_IN
- 库存/售价修改按钮：STOCK_EDIT
- 账目管理：ACCOUNT_VIEW
- 用户管理：USER_MGR
- 角色管理：ROLE_MGR

### 4.3 Axios 规则
- 请求：自动加 `Authorization: Bearer <token>`
- 响应：
  - 401：清 token → /login
  - 403：提示无权限 → /403

---

## 5. 后端安全设计（Spring Security + JWT）

### 5.1 登录
- POST /api/auth/login：校验用户名密码 → 签发 JWT
- 返回：token + user + permissions

### 5.2 JWT 过滤器
- 从 Authorization Bearer 解析 token
- 校验 token
- 注入 SecurityContext（authorities=permission_codes）

### 5.3 接口授权（强制）
- `@PreAuthorize("hasAuthority('STOCK_OUT')")`：出库
- `@PreAuthorize("hasAuthority('STOCK_IN')")`：入库
- `@PreAuthorize("hasAuthority('STOCK_EDIT')")`：库存/售价修改
- `@PreAuthorize("hasAuthority('ACCOUNT_VIEW')")`：账目
- `@PreAuthorize("hasAuthority('USER_MGR')")`：用户
- `@PreAuthorize("hasAuthority('ROLE_MGR')")`：角色

---

## 6. 数据库设计（MySQL）

> 核心：出库明细保存当时成本与售价；入库明细保存当次成本与售价；入库会更新商品当前价格。

### 6.1 表结构（逻辑）
- sys_role
- sys_user
- sys_role_permission
- product
- stock_in_record
- stock_in_item（**必须含 sale_price**）
- stock_out_record
- stock_out_item（保存成本+售价）
- stock_adjust_record

---

## 7. API 契约（前后端对接）

### 7.1 统一响应（强制）
- 成功：`{ code:0, message:"ok", data:{...} }`
- 失败：`code != 0`

建议错误码：
- 401 未登录/Token 过期
- 403 无权限
- 1001 参数校验失败
- 2001 商品不存在
- 2002 库存不足
- 500 系统异常

### 7.2 认证
**POST** `/api/auth/login`
```json
{ "username":"admin", "password":"123456" }
```
返回 data：
```json
{
  "token":"jwt",
  "user": { "id":1, "username":"admin", "name":"超级管理员", "roleId":1 },
  "permissions": ["STOCK_IN","STOCK_OUT","STOCK_EDIT","ACCOUNT_VIEW","USER_MGR","ROLE_MGR"]
}
```

**GET** `/api/auth/me`：返回 user + permissions

### 7.3 首页营业额
**GET** `/api/dashboard/revenue`
```json
{ "todayRevenue": 1234.5, "monthRevenue": 56789.0 }
```

### 7.4 商品/库存
**GET** `/api/products/by-barcode?barcode=xxx`（首页扫码展示库存依赖）
```json
{
  "id": 11,
  "barcode":"690000000001",
  "name":"可乐",
  "category":"饮料",
  "costPrice":2.00,
  "salePrice":3.50,
  "stock":18,
  "status":1
}
```
**GET** `/api/products?page&size&keyword`

**PUT** `/api/products/{id}/price`（STOCK_EDIT）

**PUT** `/api/products/{id}/stock-adjust`（STOCK_EDIT）
```json
{ "changeStock": 10, "reason": "盘点调整" }
```

### 7.5 入库（STOCK_IN）
**POST** `/api/stock-in`（items 必须含 qty、costPrice、salePrice 且均>0）
```json
{
  "items": [
    { "barcode":"690000000001", "qty":5, "costPrice":2.30, "salePrice":3.80 }
  ]
}
```

**POST** `/api/products`（新增商品，建议 STOCK_IN；costPrice/salePrice >0）
```json
{
  "barcode":"690000000999",
  "name":"新商品",
  "category":"零食",
  "costPrice":1.20,
  "salePrice":2.00,
  "status":1
}
```

### 7.6 出库/销售（STOCK_OUT，多商品购物车）
**POST** `/api/stock-out`
```json
{
  "items": [
    { "barcode":"690000000001", "qty":2 },
    { "barcode":"690000000002", "qty":1 }
  ]
}
```
返回：
```json
{ "outNo":"SO20260202xxxx", "totalAmount":8.50, "totalProfit":2.50 }
```

### 7.7 账目管理（ACCOUNT_VIEW）
**GET** `/api/accounts/summary?beginDate=yyyy-MM-dd&endDate=yyyy-MM-dd`
```json
{ "salesAmount": 10000.00, "profitAmount": 2300.00 }
```

**GET** `/api/stock-out/items?page&size&beginDate&endDate`

### 7.8 用户/角色管理
用户（USER_MGR）：
- GET /api/users
- POST /api/users
- PUT /api/users/{id}
- PUT /api/users/{id}/status
- PUT /api/users/{id}/reset-password
- DELETE /api/users/{id}（可选软删）

角色（ROLE_MGR）：
- GET /api/roles
- POST /api/roles
- PUT /api/roles/{id}
- DELETE /api/roles/{id}
- GET /api/roles/{id}/permissions
- PUT /api/roles/{id}/permissions

---

## 8. 后端强制架构规范

### 8.1 严格三层架构（必须）
- Controller：只做参数接收/校验、调用 Service、返回 Result
- Service：业务逻辑编排、事务控制（@Transactional）、调用 Mapper
- DAO/Mapper：数据库 CRUD 与查询（BaseMapper + 自定义 SQL）
- 禁止：Controller 直接操作数据库；Service 拼 SQL 字符串（使用 MP Wrapper 或 XML/注解 SQL）

### 8.2 Entity / DTO / VO 强制分离（必须）
- 每张表必须有 Entity（仅映射数据库字段）
- Controller 入参必须 DTO（带校验注解）
- Controller 出参必须 VO（禁止直接返回 Entity）
- Entity ↔ DTO/VO 必须有 Convert（MapStruct 或手写 Converter）

### 8.3 统一响应与异常（必须）
- 统一 Result<T> 返回
- `@RestControllerAdvice` 统一处理校验异常（1001）、业务异常（2001/2002）、401/403、系统异常（500）

### 8.4 事务（必须在 Service 层）
- 入库、出库、库存调整：Service 方法 `@Transactional`
- Controller 禁止事务

---

## 9. 初始化数据（admin/角色/权限）

### 9.1 admin 账号
- username：`admin`
- password：`123456`（BCrypt）

> BCrypt 密文（10 rounds）：  
> `$2b$10$wNETgw6j55nPzYoiku51MeZ/mm8RgKH4luMxJ2LunFg7zOMnwtSQ.`

### 9.2 权限点
- STOCK_IN / STOCK_OUT / STOCK_EDIT / ACCOUNT_VIEW / USER_MGR / ROLE_MGR

---

## 10. 前端对接配置（Vite Proxy）

后端端口：`8080`  
前端统一请求：`/api/**` → 代理到 `http://localhost:8080`

```ts
import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});
```

---

## 11. 验收标准（必须通过）
1. 首页任意位置扫码：购物车新增行或 qty+1。  
2. 连扫同一商品 3 次：qty=3，小计与总价正确。  
3. 鼠标把 qty 改为 5，再扫一次：qty=6。  
4. 支持多商品同时存在，总价为所有小计之和。  
5. 展示库存 stock；qty>stock 行提示明显且确认按钮不可用。  
6. 后端库存不足返回明确错误，前端提示并保留购物车供修改。  
7. 出库成功后清空购物车并刷新营业额。  
8. 入库必须填写 qty、进价、售价（均>0），入库成功更新商品当前价格。  
9. 无权限不显示菜单/按钮；接口强校验 403；前端跳 /403。

---

## 12. CodeX 任务拆分建议
A. 后端基础：工程 + MP + Security+JWT + Result/异常 + 初始化 SQL  
B. 核心业务：商品/入库/出库/营业额统计  
C. 管理与权限：用户、角色、权限分配  
D. 前端页面：登录、Layout、首页扫码购物车、入库、库存、账目、用户/角色  

---

## 附录 A：建表 SQL（MySQL 8.x）

```sql
CREATE DATABASE IF NOT EXISTS store_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE store_db;

DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
  id BIGINT NOT NULL AUTO_INCREMENT,
  role_name VARCHAR(50) NOT NULL,
  role_code VARCHAR(50) NOT NULL UNIQUE,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  name VARCHAR(50) NOT NULL,
  phone VARCHAR(20) NULL,
  role_id BIGINT NOT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS sys_role_permission;
CREATE TABLE sys_role_permission (
  id BIGINT NOT NULL AUTO_INCREMENT,
  role_id BIGINT NOT NULL,
  permission_code VARCHAR(50) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  INDEX idx_role_id (role_id),
  INDEX idx_permission_code (permission_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS product;
CREATE TABLE product (
  id BIGINT NOT NULL AUTO_INCREMENT,
  barcode VARCHAR(64) NOT NULL UNIQUE,
  name VARCHAR(100) NOT NULL,
  category VARCHAR(50) NOT NULL,
  cost_price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  sale_price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  stock INT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS stock_in_record;
CREATE TABLE stock_in_record (
  id BIGINT NOT NULL AUTO_INCREMENT,
  in_no VARCHAR(32) NOT NULL UNIQUE,
  operator_id BIGINT NOT NULL,
  total_cost DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  INDEX idx_operator_id (operator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS stock_in_item;
CREATE TABLE stock_in_item (
  id BIGINT NOT NULL AUTO_INCREMENT,
  record_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  barcode VARCHAR(64) NOT NULL,
  qty INT NOT NULL,
  cost_price DECIMAL(10,2) NOT NULL,
  sale_price DECIMAL(10,2) NOT NULL,
  amount DECIMAL(12,2) NOT NULL,
  PRIMARY KEY (id),
  INDEX idx_record_id (record_id),
  INDEX idx_product_id (product_id),
  INDEX idx_barcode (barcode)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS stock_out_record;
CREATE TABLE stock_out_record (
  id BIGINT NOT NULL AUTO_INCREMENT,
  out_no VARCHAR(32) NOT NULL UNIQUE,
  operator_id BIGINT NOT NULL,
  total_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  total_profit DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  INDEX idx_operator_id (operator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS stock_out_item;
CREATE TABLE stock_out_item (
  id BIGINT NOT NULL AUTO_INCREMENT,
  record_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  barcode VARCHAR(64) NOT NULL,
  product_name VARCHAR(100) NOT NULL,
  qty INT NOT NULL,
  cost_price DECIMAL(10,2) NOT NULL,
  sale_price DECIMAL(10,2) NOT NULL,
  amount DECIMAL(12,2) NOT NULL,
  profit DECIMAL(12,2) NOT NULL,
  PRIMARY KEY (id),
  INDEX idx_record_id (record_id),
  INDEX idx_product_id (product_id),
  INDEX idx_barcode (barcode)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS stock_adjust_record;
CREATE TABLE stock_adjust_record (
  id BIGINT NOT NULL AUTO_INCREMENT,
  adj_no VARCHAR(32) NOT NULL UNIQUE,
  operator_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  barcode VARCHAR(64) NOT NULL,
  before_stock INT NOT NULL,
  change_stock INT NOT NULL,
  after_stock INT NOT NULL,
  reason VARCHAR(255) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  INDEX idx_operator_id (operator_id),
  INDEX idx_product_id (product_id),
  INDEX idx_barcode (barcode)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 附录 B：初始化 SQL（admin/角色/权限）

```sql
USE store_db;

INSERT INTO sys_role (id, role_name, role_code) VALUES
  (1, '店长', 'STORE_MANAGER'),
  (2, '收银员', 'CASHIER'),
  (3, '仓库员', 'WAREHOUSE'),
  (4, '财务', 'FINANCE'),
  (5, '管理员', 'ADMIN')
ON DUPLICATE KEY UPDATE role_name=VALUES(role_name), role_code=VALUES(role_code);

DELETE FROM sys_role_permission WHERE role_id IN (1,2,3,4,5);

INSERT INTO sys_role_permission (role_id, permission_code) VALUES
  (1, 'STOCK_IN'),
  (1, 'STOCK_OUT'),
  (1, 'STOCK_EDIT'),
  (1, 'ACCOUNT_VIEW'),
  (1, 'USER_MGR'),
  (1, 'ROLE_MGR');

INSERT INTO sys_role_permission (role_id, permission_code) VALUES
  (2, 'STOCK_OUT');

INSERT INTO sys_role_permission (role_id, permission_code) VALUES
  (3, 'STOCK_IN');

INSERT INTO sys_role_permission (role_id, permission_code) VALUES
  (4, 'ACCOUNT_VIEW');

INSERT INTO sys_role_permission (role_id, permission_code) VALUES
  (5, 'USER_MGR'),
  (5, 'ROLE_MGR');

INSERT INTO sys_user (id, username, password, name, phone, role_id, status) VALUES
  (1, 'admin', '$2b$10$wNETgw6j55nPzYoiku51MeZ/mm8RgKH4luMxJ2LunFg7zOMnwtSQ.', '超级管理员', NULL, 1, 1)
ON DUPLICATE KEY UPDATE
  password=VALUES(password),
  name=VALUES(name),
  role_id=VALUES(role_id),
  status=VALUES(status);
```

---

## 附录 C：快速检查（验证初始化是否成功）

1) 执行 **附录A** 建表 SQL  
2) 执行 **附录B** 初始化 SQL  
3) 启动后端（端口 8080）  
4) 调用登录接口：
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}
```
应返回 token + permissions（店长全权限）
