# 工作管理系统 (Work Management System)

前后端分离的工作管理系统，包含**任务管理**和**人员管理**两大子系统。

- **前端**：Vue 3 + Vite + Element Plus + Pinia（`src/`）
- **后端**：Spring Boot 2.7 + MyBatis-Plus + MySQL（`backend/`）

---

## 技术栈

| 端 | 技术 | 用途 |
|---|---|---|
| 前端 | Vue 3 / Vite / Element Plus / Pinia / Vue Router / Axios | SPA 界面与状态管理 |
| 后端 | Spring Boot 2.7 / MyBatis-Plus 3.5 | REST API |
| 数据库 | MySQL 8.x | 数据持久化 |
| 认证 | Token（登录后内存会话，Authorization: Bearer） | 接口鉴权 |

---

## 功能概览

### 任务管理
- 任务 CRUD、状态流转（待处理 → 进行中 → 已完成，支持重新打开）
- 优先级（高/中/低）、分类标签、人员指派、截止日期与逾期识别
- 操作时间线（创建、字段变更、状态变更均落库记录）
- 按状态 / 优先级 / 人员维度的统计分析

### 人员管理
- 人员档案（姓名、职位、手机、邮箱、入职日期）
- 多级部门树形结构 CRUD（后端校验循环上级、子部门/人员占用）
- 角色权限管理（权限以 JSON 存储，角色被人员引用时禁止删除）
- 按部门筛选、姓名/职位搜索、人员关联任务查看
- 删除人员时自动解除其任务关联，避免孤儿数据

### 其他
- 仪表盘统计卡片与待办任务
- 登录认证（默认账号 **admin / admin123**），路由守卫 + 后端拦截器双重校验

---

## 快速开始

### 一键启动 / 关闭（Windows）

先完成下面的第 1 步初始化数据库，然后双击项目根目录的脚本即可：

- **`start.bat`**：自动检查 Java / npm 环境、释放被占用的 8080 / 5173 端口、
  分别在独立窗口启动后端（有 Maven 用 `mvn spring-boot:run`，否则用已构建的 jar）
  和前端（首次自动 `npm install`），就绪后自动打开浏览器。
- **`stop.bat`**：按服务窗口标题关闭进程树，并兜底按端口查杀，确认端口释放。

也可手动按以下三步启动：

### 1. 准备数据库

安装 MySQL 8.x 后，执行初始化脚本（建库、建表、种子数据）：

```bash
mysql -u root -p --default-character-set=utf8mb4 < backend/src/main/resources/schema.sql
```

> 注意：必须加 `--default-character-set=utf8mb4`。Windows 中文系统的 cmd/PowerShell 默认编码是
> GBK，mysql 客户端会自动检测成 GBK，导致 UTF-8 的脚本被错误解析（报
> `Data too long for column 'name'` 或中文乱码）。

然后按你的数据库账号修改 `backend/src/main/resources/application.yml` 中的
`spring.datasource.username` / `password`（默认 root / 123456，数据库 wms_db）。

### 2. 启动后端（端口 8080）

需 JDK 8+ 与 Maven 3.6+：

```bash
cd backend
mvn spring-boot:run
```

或使用已构建的 jar：

```bash
mvn package -DskipTests
java -jar target/work-management-system-backend-1.0.0.jar
```

### 3. 启动前端（端口 5173）

```bash
# 回到项目根目录
npm install
npm run dev
```

浏览器打开 `http://localhost:5173`，使用 **admin / admin123** 登录。
开发环境下 Vite 已配置 `/api` 代理到 `http://localhost:8080`，无需关心跨域。

---

## 项目结构

```
WorkManagementSystem/
├── start.bat / stop.bat          # Windows 一键启动 / 一键关闭脚本
├── frontend/                     # 前端（Vue 3 + Vite）
│   ├── index.html
│   ├── package.json / vite.config.js
│   ├── public/
│   └── src/
│       ├── api/
│       │   ├── request.js        # axios 实例（token 注入、统一错误/401 处理）
│       │   └── index.js          # 各模块 API 封装
│       ├── router/index.js       # 路由 + 登录守卫
│       ├── stores/               # Pinia（app / task / personnel，全部异步调后端）
│       ├── components/  views/  layouts/
│       └── utils/
└── backend/                      # 后端（Spring Boot）
    ├── pom.xml
    └── src/main/
        ├── resources/
        │   ├── application.yml   # 数据源/端口/MyBatis-Plus 配置
        │   └── schema.sql        # 数据库初始化脚本
        └── java/com/wms/
            ├── WmsApplication.java
            ├── config/           # CORS、Token 鉴权拦截器、字段自动填充
            ├── common/           # 统一响应 Result、全局异常处理
            ├── entity/ mapper/ service/ controller/
            ├── dto/              # 登录请求/响应
            └── vo/               # 任务详情、统计 VO
```

---

## 主要 REST 接口

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/auth/login` | 登录，返回 token 与用户信息 |
| POST | `/api/auth/logout` | 退出登录 |
| GET | `/api/auth/me` | 当前登录用户 |
| GET/POST | `/api/departments`、`/api/departments/tree` | 部门列表 / 部门树 |
| PUT/DELETE | `/api/departments/{id}` | 编辑 / 删除部门 |
| GET/POST | `/api/roles` | 角色列表 / 新增 |
| PUT/DELETE | `/api/roles/{id}` | 编辑 / 删除角色 |
| GET/POST | `/api/personnel` | 人员列表（keyword、departmentId）/ 新增 |
| GET/PUT/DELETE | `/api/personnel/{id}` | 人员详情 / 编辑 / 删除 |
| GET/POST | `/api/tasks` | 任务列表（keyword、status、priority）/ 新增 |
| GET | `/api/tasks/statistics` | 任务统计 |
| GET/PUT/DELETE | `/api/tasks/{id}` | 任务详情（含时间线）/ 编辑 / 删除 |
| PATCH | `/api/tasks/{id}/status` | 变更任务状态 |

统一响应格式：`{ code: 200, message: '操作成功', data: ... }`，未登录返回 `code: 401`。

---

## 页面路由

| 路径 | 页面 |
|---|---|
| `/login` | 登录页 |
| `/dashboard` | 仪表盘 |
| `/task`、`/task/:id`、`/task/statistics` | 任务列表 / 详情 / 统计 |
| `/personnel`、`/personnel/:id`、`/personnel/department`、`/personnel/role` | 人员列表 / 详情 / 部门管理 / 角色权限 |
