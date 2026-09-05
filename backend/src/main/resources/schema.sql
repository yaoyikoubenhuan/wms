-- ============================================================
-- 工作管理系统（WMS）数据库初始化脚本
-- 数据库：MySQL 8.x
-- 用法：mysql -u root -p < schema.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS wms_db
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE wms_db;

-- ------------------------------------------------------------
-- 系统用户表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id          VARCHAR(32)  NOT NULL COMMENT '用户ID',
    username    VARCHAR(64)  NOT NULL COMMENT '登录用户名',
    password    VARCHAR(64)  NOT NULL COMMENT '密码（MD5）',
    name        VARCHAR(64)  NOT NULL COMMENT '姓名',
    role        VARCHAR(64)  DEFAULT NULL COMMENT '角色名称',
    avatar      VARCHAR(255) DEFAULT NULL COMMENT '头像',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '系统用户表';

-- ------------------------------------------------------------
-- 部门表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS wms_department;
CREATE TABLE wms_department (
    id          VARCHAR(32)  NOT NULL COMMENT '部门ID',
    name        VARCHAR(64)  NOT NULL COMMENT '部门名称',
    parent_id   VARCHAR(32)  DEFAULT NULL COMMENT '上级部门ID',
    description VARCHAR(255) DEFAULT NULL COMMENT '部门描述',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_parent_id (parent_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '部门表';

-- ------------------------------------------------------------
-- 角色表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS wms_role;
CREATE TABLE wms_role (
    id          VARCHAR(32)  NOT NULL COMMENT '角色ID',
    name        VARCHAR(64)  NOT NULL COMMENT '角色名称',
    permissions JSON         DEFAULT NULL COMMENT '权限key列表（JSON数组）',
    description VARCHAR(255) DEFAULT NULL COMMENT '角色描述',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '角色表';

-- ------------------------------------------------------------
-- 人员表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS wms_person;
CREATE TABLE wms_person (
    id            VARCHAR(32)  NOT NULL COMMENT '人员ID',
    name          VARCHAR(64)  NOT NULL COMMENT '姓名',
    phone         VARCHAR(32)  DEFAULT NULL COMMENT '手机号',
    email         VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    department_id VARCHAR(32)  DEFAULT NULL COMMENT '部门ID',
    role_id       VARCHAR(32)  DEFAULT 'role-member' COMMENT '角色ID',
    position      VARCHAR(64)  DEFAULT NULL COMMENT '职位',
    entry_date    DATE         DEFAULT NULL COMMENT '入职日期',
    status        VARCHAR(16)  DEFAULT 'active' COMMENT '状态：active在职/inactive离职',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_department (department_id),
    KEY idx_role (role_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '人员表';

-- ------------------------------------------------------------
-- 任务表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS wms_task;
CREATE TABLE wms_task (
    id             VARCHAR(32)  NOT NULL COMMENT '任务ID',
    title          VARCHAR(128) NOT NULL COMMENT '任务标题',
    description    TEXT         DEFAULT NULL COMMENT '任务描述',
    status         VARCHAR(16)  DEFAULT 'pending' COMMENT '状态：pending/inProgress/completed',
    priority       VARCHAR(16)  DEFAULT 'medium' COMMENT '优先级：high/medium/low',
    category       VARCHAR(64)  DEFAULT NULL COMMENT '分类标签',
    assignee_id    VARCHAR(32)  DEFAULT NULL COMMENT '负责人ID',
    department_id  VARCHAR(32)  DEFAULT NULL COMMENT '部门ID',
    deadline       DATE         DEFAULT NULL COMMENT '截止日期',
    completed_time DATETIME     DEFAULT NULL COMMENT '完成时间',
    create_time    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_assignee (assignee_id),
    KEY idx_status (status),
    KEY idx_priority (priority)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '任务表';

-- ------------------------------------------------------------
-- 任务操作时间线表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS wms_task_timeline;
CREATE TABLE wms_task_timeline (
    id          VARCHAR(32)  NOT NULL COMMENT '记录ID',
    task_id     VARCHAR(32)  NOT NULL COMMENT '任务ID',
    action      VARCHAR(16)  NOT NULL COMMENT '动作：created/updated/completed',
    detail      VARCHAR(255) DEFAULT NULL COMMENT '详情',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (id),
    KEY idx_task_id (task_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '任务操作时间线表';

-- ============================================================
-- 种子数据
-- ============================================================

-- 默认管理员账号：admin / admin123
INSERT INTO sys_user (id, username, password, name, role, avatar) VALUES
('user-admin', 'admin', MD5('admin123'), '系统管理员', '管理员', '');

-- 部门
INSERT INTO wms_department (id, name, parent_id, description) VALUES
('dept-1',   '技术部', NULL,       '负责技术研发'),
('dept-2',   '产品部', NULL,       '负责产品设计与规划'),
('dept-3',   '设计部', NULL,       '负责UI/UX设计'),
('dept-4',   '市场部', NULL,       '负责市场推广'),
('dept-1-1', '前端组', 'dept-1',   '前端开发'),
('dept-1-2', '后端组', 'dept-1',   '后端开发');

-- 角色
INSERT INTO wms_role (id, name, permissions, description) VALUES
('role-admin',   '管理员',   JSON_ARRAY('all'),                                             '系统管理员，拥有所有权限'),
('role-manager', '部门经理', JSON_ARRAY('task.assign', 'task.view', 'task.manage', 'personnel.view'), '管理部门和任务'),
('role-member',  '普通成员', JSON_ARRAY('task.view', 'task.updateOwn'),                     '查看和更新自己的任务');

-- 人员
INSERT INTO wms_person (id, name, phone, email, department_id, role_id, position, entry_date, status) VALUES
('person-1', '张伟', '13800000001', 'zhangwei@example.com',  'dept-1-1', 'role-manager', '前端组长',   '2022-03-15', 'active'),
('person-2', '李娜', '13800000002', 'lina@example.com',      'dept-1-1', 'role-member',  '前端工程师', '2023-07-01', 'active'),
('person-3', '王强', '13800000003', 'wangqiang@example.com', 'dept-1-2', 'role-manager', '后端组长',   '2021-11-20', 'active'),
('person-4', '刘洋', '13800000004', 'liuyang@example.com',   'dept-2',   'role-member',  '产品经理',   '2023-02-10', 'active'),
('person-5', '陈静', '13800000005', 'chenjing@example.com',  'dept-3',   'role-member',  'UI设计师',   '2024-05-06', 'active');

-- 任务
INSERT INTO wms_task (id, title, description, status, priority, category, assignee_id, department_id, deadline, completed_time, create_time) VALUES
('task-1', '完成登录页面开发', '实现账号密码登录、表单校验与登录态保持', 'completed', 'high',   '需求', 'person-2', 'dept-1-1', '2026-08-20', '2026-08-18 16:30:00', '2026-08-10 09:00:00'),
('task-2', '任务列表接口联调', '前后端联调任务列表筛选与分页', 'inProgress', 'high',   'Bug',  'person-3', 'dept-1-2', '2026-09-10', NULL, '2026-08-28 14:00:00'),
('task-3', '仪表盘统计图表优化', '优化统计卡片与进度条展示', 'pending',    'medium', '优化', 'person-1', 'dept-1-1', '2026-09-20', NULL, '2026-09-01 10:30:00'),
('task-4', '人员管理需求评审',   '评审人员增删改查与部门树交互', 'pending',  'low',    '需求', 'person-4', 'dept-2',   '2026-09-15', NULL, '2026-09-02 11:00:00'),
('task-5', '设计系统组件规范',   '输出 Element Plus 组件使用规范', 'inProgress', 'medium', '需求', 'person-5', 'dept-3', '2026-09-08', NULL, '2026-08-25 15:20:00');

-- 任务时间线
INSERT INTO wms_task_timeline (id, task_id, action, detail, create_time) VALUES
('tl-1', 'task-1', 'created',   '任务创建',                '2026-08-10 09:00:00'),
('tl-2', 'task-1', 'updated',   '状态由「待处理」变更为「进行中」', '2026-08-12 10:00:00'),
('tl-3', 'task-1', 'completed', '任务标记为已完成',         '2026-08-18 16:30:00'),
('tl-4', 'task-2', 'created',   '任务创建',                '2026-08-28 14:00:00'),
('tl-5', 'task-2', 'updated',   '状态由「待处理」变更为「进行中」', '2026-08-30 09:30:00'),
('tl-6', 'task-3', 'created',   '任务创建',                '2026-09-01 10:30:00'),
('tl-7', 'task-4', 'created',   '任务创建',                '2026-09-02 11:00:00'),
('tl-8', 'task-5', 'created',   '任务创建',                '2026-08-25 15:20:00'),
('tl-9', 'task-5', 'updated',   '状态由「待处理」变更为「进行中」', '2026-08-26 09:00:00');
