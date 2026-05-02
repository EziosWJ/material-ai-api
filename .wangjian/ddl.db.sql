--  核心规则：
-- 不使用数据库外键
-- 使用 BIGINT 自增主键
-- 逻辑删除字段统一为 deleted
-- 启用禁用字段统一为 status
-- 内置数据字段统一为 is_builtin
-- 数据库字段使用 snake_case
-- Java / JSON 字段后续使用 camelCase
--  补充说明：
-- 1. 所有关联字段都没有加 FOREIGN KEY。
-- 2. 删除约束由后端 Service 层控制。
-- 3. 关联字段已保留普通索引。
-- 4. 日志表不设计 deleted 字段，因为前面已确定：日志不做单条删除，只提供开发期清空接口，清空时物理删除。
-- 5. permission_code 允许为空，MySQL 唯一索引允许多个 NULL；如果你希望所有菜单都必须有权限标识，可改为 NOT NULL。
-- =========================================================
-- 1. 用户表
-- =========================================================
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',

    username VARCHAR(50) NOT NULL COMMENT '用户名/登录账号',
    nickname VARCHAR(50) NOT NULL COMMENT '用户昵称',
    password VARCHAR(100) NOT NULL COMMENT '密码密文',
    phone VARCHAR(20) NULL COMMENT '手机号',
    email VARCHAR(100) NULL COMMENT '邮箱',
    avatar VARCHAR(255) NULL COMMENT '头像URL',
    gender VARCHAR(20) NOT NULL DEFAULT 'UNSPECIFIED' COMMENT '性别：UNSPECIFIED/MALE/FEMALE',

    dept_id BIGINT NULL COMMENT '部门ID',

    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
    is_builtin TINYINT NOT NULL DEFAULT 0 COMMENT '是否内置：1是，0否',

    last_login_time DATETIME NULL COMMENT '最后登录时间',
    last_login_ip VARCHAR(45) NULL COMMENT '最后登录IP',

    remark VARCHAR(500) NULL COMMENT '备注',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT NULL COMMENT '创建人ID',
    update_by BIGINT NULL COMMENT '更新人ID',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常，1删除',

    UNIQUE KEY uk_username (username),
    KEY idx_dept_id (dept_id),
    KEY idx_deleted_status (deleted, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';


-- =========================================================
-- 2. 角色表
-- =========================================================
CREATE TABLE sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',

    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',

    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    is_builtin TINYINT NOT NULL DEFAULT 0 COMMENT '是否内置：1是，0否',

    remark VARCHAR(500) NULL COMMENT '备注',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT NULL COMMENT '创建人ID',
    update_by BIGINT NULL COMMENT '更新人ID',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常，1删除',

    UNIQUE KEY uk_role_code (role_code),
    KEY idx_deleted_status_sort (deleted, status, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';


-- =========================================================
-- 3. 菜单表
-- =========================================================
CREATE TABLE sys_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '菜单ID',

    parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父级菜单ID，根节点为0',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    menu_type VARCHAR(20) NOT NULL COMMENT '菜单类型：DIR/MENU/LINK',

    path VARCHAR(255) NULL COMMENT '路由路径',
    component VARCHAR(255) NULL COMMENT '前端组件路径',
    external_url VARCHAR(500) NULL COMMENT '外链地址',
    icon VARCHAR(100) NULL COMMENT '图标',

    permission_code VARCHAR(100) NULL COMMENT '权限标识',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    visible TINYINT NOT NULL DEFAULT 1 COMMENT '是否显示：1显示，0隐藏',

    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
    is_builtin TINYINT NOT NULL DEFAULT 0 COMMENT '是否内置：1是，0否',

    remark VARCHAR(500) NULL COMMENT '备注',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT NULL COMMENT '创建人ID',
    update_by BIGINT NULL COMMENT '更新人ID',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常，1删除',

    UNIQUE KEY uk_permission_code (permission_code),
    KEY idx_parent_id (parent_id),
    KEY idx_deleted_status_sort (deleted, status, sort_order),
    KEY idx_menu_type (menu_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';


-- =========================================================
-- 4. 部门表
-- =========================================================
CREATE TABLE sys_dept (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '部门ID',

    parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父级部门ID，根节点为0',
    dept_name VARCHAR(100) NOT NULL COMMENT '部门名称',
    dept_code VARCHAR(50) NOT NULL COMMENT '部门编码',

    leader VARCHAR(50) NULL COMMENT '负责人',
    phone VARCHAR(20) NULL COMMENT '联系电话',
    email VARCHAR(100) NULL COMMENT '邮箱',

    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
    is_builtin TINYINT NOT NULL DEFAULT 0 COMMENT '是否内置：1是，0否',

    remark VARCHAR(500) NULL COMMENT '备注',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT NULL COMMENT '创建人ID',
    update_by BIGINT NULL COMMENT '更新人ID',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常，1删除',

    UNIQUE KEY uk_dept_code (dept_code),
    KEY idx_parent_id (parent_id),
    KEY idx_deleted_status_sort (deleted, status, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';


-- =========================================================
-- 5. 用户角色关联表
-- =========================================================
CREATE TABLE sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',

    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by BIGINT NULL COMMENT '创建人ID',

    UNIQUE KEY uk_user_role (user_id, role_id),
    KEY idx_user_id (user_id),
    KEY idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';


-- =========================================================
-- 6. 角色菜单关联表
-- =========================================================
CREATE TABLE sys_role_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',

    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by BIGINT NULL COMMENT '创建人ID',

    UNIQUE KEY uk_role_menu (role_id, menu_id),
    KEY idx_role_id (role_id),
    KEY idx_menu_id (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';


-- =========================================================
-- 7. 字典类型表
-- =========================================================
CREATE TABLE sys_dict_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '字典类型ID',

    dict_name VARCHAR(100) NOT NULL COMMENT '字典名称',
    dict_code VARCHAR(100) NOT NULL COMMENT '字典编码',

    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    is_builtin TINYINT NOT NULL DEFAULT 0 COMMENT '是否内置：1是，0否',

    remark VARCHAR(500) NULL COMMENT '备注',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT NULL COMMENT '创建人ID',
    update_by BIGINT NULL COMMENT '更新人ID',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常，1删除',

    UNIQUE KEY uk_dict_code (dict_code),
    KEY idx_deleted_status_sort (deleted, status, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';


-- =========================================================
-- 8. 字典数据表
-- =========================================================
CREATE TABLE sys_dict_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '字典数据ID',

    dict_type_id BIGINT NOT NULL COMMENT '字典类型ID',
    dict_label VARCHAR(100) NOT NULL COMMENT '字典标签',
    dict_value VARCHAR(100) NOT NULL COMMENT '字典值',

    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',

    remark VARCHAR(500) NULL COMMENT '备注',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT NULL COMMENT '创建人ID',
    update_by BIGINT NULL COMMENT '更新人ID',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常，1删除',

    UNIQUE KEY uk_type_value (dict_type_id, dict_value),
    KEY idx_dict_type_id (dict_type_id),
    KEY idx_deleted_sort (deleted, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';


-- =========================================================
-- 9. 登录日志表
-- =========================================================
CREATE TABLE sys_login_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '登录日志ID',

    username VARCHAR(50) NOT NULL COMMENT '用户名',
    login_status VARCHAR(20) NOT NULL COMMENT '登录状态：SUCCESS/FAIL',

    login_ip VARCHAR(45) NULL COMMENT '登录IP',
    login_location VARCHAR(100) NULL COMMENT '登录地点',
    browser VARCHAR(100) NULL COMMENT '浏览器',
    os VARCHAR(100) NULL COMMENT '操作系统',
    user_agent VARCHAR(500) NULL COMMENT 'User-Agent',

    message VARCHAR(500) NULL COMMENT '提示信息',
    login_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    KEY idx_username (username),
    KEY idx_login_status (login_status),
    KEY idx_login_time (login_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';


-- =========================================================
-- 10. 操作日志表
-- =========================================================
CREATE TABLE sys_oper_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '操作日志ID',

    module_name VARCHAR(100) NOT NULL COMMENT '模块名称',
    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型：CREATE/UPDATE/DELETE/IMPORT/EXPORT',

    request_method VARCHAR(20) NULL COMMENT '请求方法',
    request_url VARCHAR(500) NULL COMMENT '请求URL',

    operator_id BIGINT NULL COMMENT '操作人ID',
    operator_name VARCHAR(50) NULL COMMENT '操作人名称',
    operator_ip VARCHAR(45) NULL COMMENT '操作IP',
    operator_location VARCHAR(100) NULL COMMENT '操作地点',

    request_params TEXT NULL COMMENT '请求参数',
    response_result TEXT NULL COMMENT '响应结果摘要',

    cost_time BIGINT NULL COMMENT '耗时，单位毫秒',
    operation_status VARCHAR(20) NOT NULL COMMENT '操作状态：SUCCESS/FAIL',
    error_message TEXT NULL COMMENT '错误信息',

    operation_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    KEY idx_module_name (module_name),
    KEY idx_operation_type (operation_type),
    KEY idx_operator_id (operator_id),
    KEY idx_operation_status (operation_status),
    KEY idx_operation_time (operation_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';


-- =========================================================
-- 11. 文件表
-- =========================================================
CREATE TABLE sys_file (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '文件ID',

    original_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    storage_name VARCHAR(255) NOT NULL COMMENT '存储文件名',
    extension VARCHAR(50) NULL COMMENT '文件扩展名',
    mime_type VARCHAR(100) NULL COMMENT 'MIME类型',

    file_size BIGINT NOT NULL DEFAULT 0 COMMENT '文件大小，单位字节',
    file_md5 VARCHAR(32) NULL COMMENT '文件MD5',

    storage_path VARCHAR(500) NOT NULL COMMENT '存储相对路径',
    access_url VARCHAR(500) NULL COMMENT '访问URL',

    business_module VARCHAR(50) NULL COMMENT '所属业务模块，如avatar/import/attachment',

    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',

    remark VARCHAR(500) NULL COMMENT '备注',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT NULL COMMENT '创建人ID',
    update_by BIGINT NULL COMMENT '更新人ID',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常，1删除',

    KEY idx_file_md5 (file_md5),
    KEY idx_business_module (business_module),
    KEY idx_deleted_status (deleted, status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件表';