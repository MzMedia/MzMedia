-- ----------------------------
-- 设备信息表
-- ----------------------------
drop table if exists device;
create table device
(
    tenant_id        varchar(20)          default '000000' comment '租户编号',
    user_id          bigint(20)  not null comment '用户ID',
    sub_uid          bigint(20)  not null comment '子用户ID',
    device_id        bigint(20)  not null comment '设备ID',
    sub_did          bigint(20)  not null comment '子设备ID',
    device_name      varchar(30) not null comment '设备名称',
    nick_name        varchar(30) not null comment '设备昵称',
    productKey       varchar(30) not null comment '产品key',
    serial_number    varchar(64) not null comment '设备编号',
    secret           varchar(30) not null comment '产品密钥',
    firmware_version varchar(30) not null comment '固件版本',
    locate           json        not null comment '设备位置',
    tag              varchar(30) not null comment '设备标签',
    status           tinyint(1)  not null default 1 comment '设备状态（1-未激活，2-禁用，3-在线，4-离线）',
    ip               varchar(32) null     default null comment '设备IP',
    active_time      datetime    null     default null comment '激活时间',
    things_model     json        null comment '物模型值',
    summary          json        null comment '设备摘要',
    del_flag         char(1)              default '0' comment '删除标志（0代表存在 2代表删除）',
    create_dept      bigint(20)           default null comment '创建部门',
    create_by        bigint(20)           default null comment '创建者',
    create_time      datetime comment '创建时间',
    update_by        bigint(20)           default null comment '更新者',
    update_time      datetime comment '更新时间',
    remark           varchar(500)         default null comment '备注',
    primary key (user_id)
) engine = innodb comment = '设备信息表';

DROP TABLE IF EXISTS `device_group`;
CREATE TABLE `device_group`
(
    `tenant_id` varchar(20) default '000000' comment '租户编号',
    `device_id` bigint(20) NOT NULL COMMENT '设备ID',
    `group_id`  bigint(20) NOT NULL COMMENT '分组ID',
    PRIMARY KEY (`device_id`, `group_id`) USING BTREE
) ENGINE = InnoDB COMMENT = '设备分组'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- 设备组
-- ----------------------------
DROP TABLE IF EXISTS `group`;
CREATE TABLE `group`
(
    `tenant_id`   varchar(20)           default '000000' comment '租户编号',
    `user_id`     bigint(20)   not null comment '用户ID',
    `group_id`    bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '分组ID',
    `group_name`  varchar(64)  NOT NULL COMMENT '分组名称',
    `group_order` tinyint(11)  NOT NULL DEFAULT 0 COMMENT '分组排序',
    `del_flag`    char(1)      NULL     DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    `create_dept` bigint(20)            default null comment '创建部门',
    `create_by`   varchar(64)  NULL     DEFAULT '' COMMENT '创建者',
    `create_time` datetime(0)  NULL     DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(64)  NULL     DEFAULT '' COMMENT '更新者',
    `update_time` datetime(0)  NULL     DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(500) NULL     DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`group_id`) USING BTREE,
    INDEX `dev_group_index_user_id` (`user_id`) USING BTREE,
    INDEX `dev_group_index_tenant_id` (`tenant_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 7 COMMENT = '设备分组'
  ROW_FORMAT = Dynamic;
-- ----------------------------
-- 设备用户表
-- ----------------------------
DROP TABLE IF EXISTS `device_user`;
CREATE TABLE `device_user`
(
    `tenant_id`   bigint(20)                                              NOT NULL COMMENT '租户ID',
    `user_id`     bigint(20)                                              NOT NULL COMMENT '用户ID',
    `user_name`   varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL DEFAULT '' COMMENT '用户昵称',
    `device_id`   bigint(20)                                              NOT NULL COMMENT '设备ID',
    `phonenumber` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL COMMENT '手机号码',
    `is_owner`    tinyint(11)                                             NOT NULL COMMENT '是否为设备所有者（0=否，1=是）',
    `perms`       varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '用户物模型权限，多个以英文逗号分隔',
    `del_flag`    char(1) CHARACTER SET utf8 COLLATE utf8_general_ci      NULL     DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    `create_by`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT '' COMMENT '创建者',
    `create_time` datetime(0)                                             NULL     DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT '' COMMENT '更新者',
    `update_time` datetime(0)                                             NULL     DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`device_id`, `user_id`) USING BTREE,
    INDEX `dev_user_index_user_id` (`user_id`) USING BTREE,
    INDEX `dev_user_index_tenant_id` (`tenant_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '设备用户'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- 设备交互日志表(上下线，事件，功能调用等)
-- ----------------------------
DROP TABLE IF EXISTS `device_log`;
CREATE TABLE `device_log`
(
    `tenant_id`   bigint(20)                                              NOT NULL COMMENT '租户ID',
    `log_id`      bigint(20)                                              NOT NULL AUTO_INCREMENT COMMENT '设备监测信息ID',
    `device_id`   bigint(20)                                              NULL DEFAULT NULL COMMENT '设备ID',
    `identity`    varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '标识符',
    `model_name`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物模型名称',
    `log_type`    tinyint(1)                                              NOT NULL COMMENT '类型（1=属性上报，2=调用功能，3=事件上报，4=设备升级，5=设备上线，6=设备离线）',
    `log_value`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '日志值',
    `create_by`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime(0)                                             NULL DEFAULT NULL COMMENT '创建时间',
    `remark`      varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`log_id`) USING BTREE,
    INDEX `device_log_index_tenant_id` (`tenant_id`) USING BTREE,
    INDEX `device_log_index_device_id` (`device_id`) USING BTREE,
    INDEX `index_device_id_createTime` (`device_id`, `create_time`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '设备日志'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- 设备告警日志表
-- ----------------------------
DROP TABLE IF EXISTS `alert_log`;
CREATE TABLE `alert_log`
(
    `tenant_id`    bigint(20)                                              NOT NULL COMMENT '租户ID',
    `alert_log_id` bigint(20)                                              NOT NULL AUTO_INCREMENT COMMENT '告警日志ID',
    `device_id`    bigint(20)                                              NULL DEFAULT NULL COMMENT '设备ID',
    `product_id`   bigint(20)                                              NULL DEFAULT NULL COMMENT '产品ID',
    `alert_name`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '告警名称',
    `alert_level`  tinyint(11)                                             NOT NULL COMMENT '告警级别（1=提醒通知，2=轻微问题，3=严重警告）',
    `detail`       json                                                    NULL COMMENT '告警详情（对应物模型）',
    `status`       tinyint(11)                                             NOT NULL COMMENT '处理状态(1=不需要处理,2=未处理,3=已处理)',
    `create_by`    varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT '' COMMENT '创建者',
    `create_time`  datetime(0)                                             NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`    varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT '' COMMENT '更新者',
    `update_time`  datetime(0)                                             NULL DEFAULT NULL COMMENT '更新时间',
    `remark`       varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`alert_log_id`) USING BTREE,
    INDEX `alert_log_index_serial_number` (`device_id`) USING BTREE,
    INDEX `alert_log_index_product_id` (`product_id`) USING BTREE,
    INDEX `alert_log_index_create_time` (`create_time`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '设备告警日志'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- 规则触发表
-- ----------------------------
DROP TABLE IF EXISTS `scene_trigger`;
CREATE TABLE `scene_trigger`
(
    `tenant_id`        bigint(20)                                             NOT NULL COMMENT '租户ID',
    `scene_trigger_id` bigint(20)                                             NOT NULL AUTO_INCREMENT COMMENT '场景触发器ID',
    `scene_id`         bigint(20)                                             NOT NULL COMMENT '所属场景ID',
    `device_id`        bigint(20)                                             NULL DEFAULT NULL COMMENT '设备ID',
    `product_id`       bigint(20)                                             NULL DEFAULT NULL COMMENT '产品ID',
    `status`           tinyint(1)                                             NOT NULL COMMENT '告警状态（1-启动，2-停止）',
    `source`           tinyint(1)                                             NOT NULL COMMENT '触发源（1=设备触发，2=定时触发）',
    `id`               varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物模型标识符',
    `name`             varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物模型名称',
    `value`            varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物模型值',
    `operator`         varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '操作符',
    `type`             tinyint(1)                                             NULL DEFAULT NULL COMMENT '物模型类别（1=属性，2=功能，3=事件，4=设备升级，5=设备上线，6=设备下线）',
    PRIMARY KEY (`scene_trigger_id`) USING BTREE,
    INDEX `scene_trigger_index_scene_id` (`scene_id`) USING BTREE,
    INDEX `scene_trigger_index_device_id` (`device_id`) USING BTREE,
    INDEX `scene_trigger_index_product_id` (`product_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '场景联动触发器'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- 场景联动规则表
-- ----------------------------
DROP TABLE IF EXISTS `scene_rule`;
CREATE TABLE `scene_rule`
(
    `tenant_id`   bigint(20)                                              NOT NULL COMMENT '租户ID',
    `scene_id`    bigint(20)                                              NOT NULL AUTO_INCREMENT COMMENT '场景ID',
    `scene_name`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '场景名称',
    `actions`     json                                                    NOT NULL COMMENT '执行动作',
    `status`      tinyint(1)                                              NULL DEFAULT NULL COMMENT '场景状态（1-启动，2-停止）',
    `create_by`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime(0)                                             NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT '' COMMENT '更新者',
    `update_time` datetime(0)                                             NULL DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`scene_id`) USING BTREE,
    INDEX `scene_rule_index_tenant_id` (`tenant_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '场景联动'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- 设备告警规则表
-- ----------------------------
DROP TABLE IF EXISTS `alert_rule`;
CREATE TABLE `alert_rule`
(
    `tenant_id`   bigint(20)                                              NOT NULL COMMENT '租户ID',
    `alert_id`    bigint(20)                                              NOT NULL AUTO_INCREMENT COMMENT '告警ID',
    `alert_name`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '告警名称',
    `alert_level` tinyint(11)                                             NOT NULL COMMENT '告警级别（1=提醒通知，2=轻微问题，3=严重警告）',
    `scene_id`    bigint(20)                                              NOT NULL COMMENT '所属场景ID',
    `create_by`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime(0)                                             NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT '' COMMENT '更新者',
    `update_time` datetime(0)                                             NULL DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`alert_id`) USING BTREE,
    INDEX `alert_rule_index_product_id` (`scene_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '设备告警规则'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- 产品表
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`
(
    `tenant_id`          bigint(20)                                              NOT NULL COMMENT '租户ID',
    `category_id`        bigint(20)                                              NOT NULL COMMENT '产品分类ID',
    `product_id`         bigint(20)                                              NOT NULL AUTO_INCREMENT COMMENT '产品ID',
    `product_name`       varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '产品名称',
    `protocol_code`      varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL COMMENT '协议编号',
    `is_sys`             tinyint(1)                                              NOT NULL DEFAULT 0 COMMENT '是否系统通用（0-否，1-是）',
    `is_authorize`       tinyint(1)                                              NOT NULL DEFAULT 0 COMMENT '是否启用授权码（0-否，1-是）',
    `mqtt_account`       varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL COMMENT 'mqtt账号',
    `mqtt_password`      varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL COMMENT 'mqtt密码',
    `mqtt_secret`        varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL COMMENT '产品秘钥',
    `status`             tinyint(1)                                              NULL     DEFAULT NULL COMMENT '状态（1-未发布，2-已发布）',
    `things_models_json` json                                                    NULL COMMENT '物模型JSON（属性、功能、事件）',
    `device_type`        tinyint(1)                                              NULL     DEFAULT 1 COMMENT '设备类型（1-直连设备、2-网关设备、3-监控设备）',
    `network_method`     tinyint(1)                                              NULL     DEFAULT 1 COMMENT '联网方式（1=wifi、2=蜂窝(2G/3G/4G/5G)、3=以太网、4=其他）',
    `vertificate_method` tinyint(1)                                              NULL     DEFAULT 1 COMMENT '认证方式（1-简单认证、2-加密认证、3-简单+加密）',
    `img_url`            varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '图片地址',
    `del_flag`           char(1) CHARACTER SET utf8 COLLATE utf8_general_ci      NULL     DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    `create_by`          varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT '' COMMENT '创建者',
    `create_time`        datetime(0)                                             NULL     DEFAULT NULL COMMENT '创建时间',
    `update_by`          varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT '' COMMENT '更新者',
    `update_time`        datetime(0)                                             NULL     DEFAULT NULL COMMENT '更新时间',
    `remark`             varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`product_id`) USING BTREE,
    INDEX `product_index_category_id` (`category_id`) USING BTREE,
    INDEX `product_index_tenant_id` (`tenant_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 131
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '产品'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- 产品分类表
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`
(
    `tenant_id`     bigint(20)                                              NOT NULL COMMENT '租户ID',
    `category_id`   bigint(20)                                              NOT NULL AUTO_INCREMENT COMMENT '产品分类ID',
    `category_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '产品分类名称',
    `is_sys`        tinyint(1)                                              NOT NULL DEFAULT 0 COMMENT '是否系统通用（0-否，1-是）',
    `parent_id`     bigint(20)                                              NULL     DEFAULT NULL COMMENT '父级ID',
    `order_num`     int(4)                                                  NULL     DEFAULT NULL COMMENT '显示顺序',
    `del_flag`      char(1) CHARACTER SET utf8 COLLATE utf8_general_ci      NULL     DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    `create_by`     varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT '' COMMENT '创建者',
    `create_time`   datetime(0)                                             NULL     DEFAULT NULL COMMENT '创建时间',
    `update_by`     varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT '' COMMENT '更新者',
    `update_time`   datetime(0)                                             NULL     DEFAULT NULL COMMENT '更新时间',
    `remark`        varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`category_id`) USING BTREE,
    INDEX `category_index_tenant_id` (`tenant_id`) USING BTREE,
    INDEX `category_index_parent_id` (`parent_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 8
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '产品分类'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- 物模型表
-- ----------------------------
DROP TABLE IF EXISTS `things_model`;
CREATE TABLE `things_model`
(
    `tenant_id`     bigint(20)                                              NOT NULL COMMENT '租户ID',
    `model_id`      bigint(20)                                              NOT NULL AUTO_INCREMENT COMMENT '物模型ID',
    `model_name`    varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '物模型名称',
    `product_id`    bigint(20)                                              NOT NULL COMMENT '产品ID',
    `identifier`    varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '标识符，产品下唯一',
    `type`          tinyint(1)                                              NOT NULL COMMENT '模型类别（1-属性，2-功能，3-事件）',
    `datatype`      varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '数据类型（integer、decimal、string、bool、array、enum）',
    `specs`         json                                                    NULL COMMENT '数据定义',
    `is_chart`      tinyint(1)                                              NOT NULL DEFAULT 0 COMMENT '是否图表展示（0-否，1-是）',
    `is_monitor`    tinyint(1)                                              NOT NULL DEFAULT 0 COMMENT '是否实时监测（0-否，1-是）',
    `is_history`    tinyint(1)                                              NULL     DEFAULT 0 COMMENT '是否历史存储（0-否，1-是）',
    `is_readonly`   tinyint(1)                                              NULL     DEFAULT 0 COMMENT '是否只读数据(0-否，1-是)',
    `is_share_perm` tinyint(1)                                              NULL     DEFAULT 0 COMMENT '是否设备分享权限(0-否，1-是)',
    `model_order`   int(10)                                                 NULL     DEFAULT 0 COMMENT '排序，值越大，排序越靠前',
    `del_flag`      char(1) CHARACTER SET utf8 COLLATE utf8_general_ci      NULL     DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    `create_by`     varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT '' COMMENT '创建者',
    `create_time`   datetime(0)                                             NULL     DEFAULT NULL COMMENT '创建时间',
    `update_by`     varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT '' COMMENT '更新者',
    `update_time`   datetime(0)                                             NULL     DEFAULT NULL COMMENT '更新时间',
    `remark`        varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`model_id`) USING BTREE,
    INDEX `things_model_index_product_id` (`product_id`) USING BTREE,
    INDEX `things_model_index_tenant_id` (`tenant_id`) USING BTREE,
    INDEX `dthings_model_index_model_order` (`model_order`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 500
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '物模型'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- 物模型模板表
-- ----------------------------
DROP TABLE IF EXISTS `things_model_template`;
CREATE TABLE `things_model_template`
(
    `tenant_id`     bigint(20)                                              NOT NULL COMMENT '租户ID',
    `template_id`   bigint(20)                                              NOT NULL AUTO_INCREMENT COMMENT '物模型ID',
    `template_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '物模型名称',
    `identifier`    varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '标识符，产品下唯一',
    `type`          tinyint(1)                                              NOT NULL COMMENT '模型类别（1-属性，2-功能，3-事件）',
    `datatype`      varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '数据类型（integer、decimal、string、bool、array、enum）',
    `specs`         json                                                    NULL COMMENT '数据定义',
    `is_sys`        tinyint(1)                                              NOT NULL DEFAULT 0 COMMENT '是否系统通用（0-否，1-是）',
    `is_chart`      tinyint(1)                                              NOT NULL DEFAULT 0 COMMENT '是否图表展示（0-否，1-是）',
    `is_monitor`    tinyint(1)                                              NOT NULL DEFAULT 0 COMMENT '是否实时监测（0-否，1-是）',
    `is_history`    tinyint(1)                                              NOT NULL DEFAULT 0 COMMENT '是否历史存储 (0-否，1-是）',
    `is_readonly`   tinyint(1)                                              NULL     DEFAULT 0 COMMENT '是否只读数据(0-否，1-是)',
    `is_share_perm` tinyint(1)                                              NULL     DEFAULT 0 COMMENT '是否设备分享权限(0-否，1-是)',
    `model_order`   int(10)                                                 NULL     DEFAULT 0 COMMENT '排序，值越大，排序越靠前',
    `del_flag`      char(1) CHARACTER SET utf8 COLLATE utf8_general_ci      NULL     DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    `create_by`     varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT '' COMMENT '创建者',
    `create_time`   datetime(0)                                             NULL     DEFAULT NULL COMMENT '创建时间',
    `update_by`     varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT '' COMMENT '更新者',
    `update_time`   datetime(0)                                             NULL     DEFAULT NULL COMMENT '更新时间',
    `remark`        varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`template_id`) USING BTREE,
    INDEX `dev_things_model_template_index_tenant_id` (`tenant_id`) USING BTREE,
    INDEX `dev_things_model_template_index_model_order` (`model_order`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 344
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '物模型模板'
  ROW_FORMAT = Dynamic;


-- ----------------------------
-- 流媒体服务器表
-- ----------------------------
DROP TABLE IF EXISTS `media_server`;
CREATE TABLE `media_server`
(
    `tenant_id`      bigint(20)                                              NOT NULL COMMENT '租户ID',
    `id`             bigint(64)                                              NOT NULL AUTO_INCREMENT COMMENT '流媒体配置ID',
    `server_id`      varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL DEFAULT '' COMMENT '服务器标识',
    `enabled`        tinyint(1)                                              NULL     DEFAULT NULL COMMENT '使能开关',
    `protocol`       varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL DEFAULT '' COMMENT '默认播放协议',
    `ip`             varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '服务器ip',
    `domain`         varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '服务器域名',
    `hookurl`        varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '回调服务器地址',
    `secret`         varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '流媒体密钥',
    `port_http`      int(11)                                                 NOT NULL DEFAULT 0 COMMENT 'http端口',
    `port_https`     int(11)                                                 NOT NULL DEFAULT 0 COMMENT 'https端口',
    `port_rtmp`      int(11)                                                 NOT NULL DEFAULT 0 COMMENT 'rtmp端口',
    `port_rtsp`      int(11)                                                 NOT NULL DEFAULT 0 COMMENT 'rtsp端口',
    `rtp_proxy_port` int(11)                                                 NOT NULL DEFAULT 0 COMMENT 'RTP收流端口',
    `port_ws`        int(11)                                                 NULL     DEFAULT NULL COMMENT 'ws端口',
    `rtp_enable`     tinyint(1)                                              NOT NULL DEFAULT 0 COMMENT '是否使用多端口模式',
    `rtp_port_range` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT 'rtp端口范围',
    `record_port`    int(11)                                                 NOT NULL DEFAULT 0 COMMENT '录像服务端口',
    `auto_config`    tinyint(1)                                              NOT NULL DEFAULT 0 COMMENT '是否自动同步配置ZLM',
    `status`         tinyint(1)                                              NOT NULL DEFAULT 0 COMMENT '状态',
    `del_flag`       char(1) CHARACTER SET utf8 COLLATE utf8_general_ci      NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    `create_by`      varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL DEFAULT '' COMMENT '创建者',
    `create_time`    datetime(0)                                             NOT NULL COMMENT '创建时间',
    `update_by`      varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT '' COMMENT '更新者',
    `update_time`    datetime(0)                                             NULL     DEFAULT NULL COMMENT '更新时间',
    `remark`         varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 8
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '流媒体服务器配置'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- sip服务器配置表
-- ----------------------------
DROP TABLE IF EXISTS `sip_config`;
CREATE TABLE `sip_config`
(
    `tenant_id`   bigint(20)                                                   NOT NULL COMMENT '租户ID',
    `id`          bigint(20)                                                   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `product_id`  bigint(20)                                                   NOT NULL COMMENT '产品ID',
    `enabled`     tinyint(1)                                                   NULL     DEFAULT NULL COMMENT '使能开关',
    `isdefault`   tinyint(1)                                                   NULL     DEFAULT NULL COMMENT '系统默认配置',
    `seniorSdp`   tinyint(1)                                                   NULL     DEFAULT NULL COMMENT '拓展sdp',
    `domain`      varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL DEFAULT '' COMMENT '服务器域',
    `sipid`       varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL DEFAULT '' COMMENT '服务器sipid',
    `password`    varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL DEFAULT '' COMMENT 'sip认证密码',
    `ip`          varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT 'sip接入IP',
    `port`        bigint(10)                                                   NULL     DEFAULT NULL COMMENT 'sip接入端口号',
    `del_flag`    char(1) CHARACTER SET utf8 COLLATE utf8_general_ci           NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    `create_by`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime(0)                                                  NOT NULL COMMENT '创建时间',
    `update_by`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NULL     DEFAULT '' COMMENT '更新者',
    `update_time` datetime(0)                                                  NULL     DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci      NULL     DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 40
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = 'sip系统配置'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- sip设备表
-- ----------------------------
DROP TABLE IF EXISTS `sip_device`;
CREATE TABLE `sip_device`
(
    `tenant_id`       bigint(20)                                                   NOT NULL COMMENT '租户ID',
    `device_id`       bigint(20)                                                   NOT NULL AUTO_INCREMENT COMMENT '设备ID',
    `product_id`      bigint(20)                                                   NOT NULL DEFAULT 0 COMMENT '产品ID',
    `device_sip_id`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL COMMENT '设备SipID',
    `device_name`     varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL DEFAULT '' COMMENT '设备名称',
    `manufacturer`    varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL DEFAULT '' COMMENT '厂商名称',
    `model`           varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL DEFAULT '' COMMENT '产品型号',
    `firmware`        varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL DEFAULT '' COMMENT '固件版本',
    `transport`       varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL DEFAULT 'UDP' COMMENT '传输模式',
    `streamMode`      varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL DEFAULT 'UDP' COMMENT '流模式',
    `online`          varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL DEFAULT '' COMMENT '在线状态',
    `registerTime`    datetime(0)                                                  NOT NULL COMMENT '注册时间',
    `lastConnectTime` datetime(0)                                                  NULL     DEFAULT NULL COMMENT '最后上线时间',
    `active_time`     datetime(0)                                                  NULL     DEFAULT NULL COMMENT '激活时间',
    `ip`              varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '设备入网IP',
    `port`            bigint(10)                                                   NULL     DEFAULT NULL COMMENT '设备接入端口号',
    `hostAddress`     varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '设备地址',
    `del_flag`        char(1) CHARACTER SET utf8 COLLATE utf8_general_ci           NULL     DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    `create_by`       varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NULL     DEFAULT '' COMMENT '创建者',
    `create_time`     datetime(0)                                                  NULL     DEFAULT NULL COMMENT '创建时间',
    `update_by`       varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NULL     DEFAULT '' COMMENT '更新者',
    `update_time`     datetime(0)                                                  NULL     DEFAULT NULL COMMENT '更新时间',
    `remark`          varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci      NULL     DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`device_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 20
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '监控设备'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- sip通道表
-- ----------------------------
DROP TABLE IF EXISTS `sip_device_channel`;
CREATE TABLE `sip_device_channel`
(
    `tenant_id`      bigint(20)                                                   NOT NULL COMMENT '租户ID',
    `id`             bigint(20)                                                   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `product_id`     bigint(20)                                                   NOT NULL DEFAULT 0 COMMENT '产品ID',
    `product_name`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL DEFAULT '' COMMENT '产品名称',
    `device_sip_id`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL COMMENT '设备SipID',
    `channel_sip_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL COMMENT '通道SipID',
    `channel_name`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL DEFAULT '' COMMENT '通道名称',
    `register_time`  datetime(0)                                                  NULL     DEFAULT NULL COMMENT '注册时间',
    `device_type`    varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NULL     DEFAULT '' COMMENT '设备类型',
    `channel_type`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NULL     DEFAULT '' COMMENT '通道类型',
    `cityCode`       varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL DEFAULT '' COMMENT '城市编码',
    `civilCode`      varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL DEFAULT '' COMMENT '行政区域',
    `manufacture`    varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL DEFAULT '' COMMENT '厂商名称',
    `model`          varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL DEFAULT '' COMMENT '产品型号',
    `owner`          varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL DEFAULT '' COMMENT '设备归属',
    `block`          varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL DEFAULT '' COMMENT '警区',
    `address`        varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL DEFAULT '' COMMENT '安装地址',
    `parentId`       varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL DEFAULT '' COMMENT '父级id',
    `ipAddress`      varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT '' COMMENT '设备入网IP',
    `port`           bigint(10)                                                   NULL     DEFAULT 0 COMMENT '设备接入端口号',
    `password`       varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL DEFAULT '' COMMENT '密码',
    `PTZType`        bigint(20)                                                   NOT NULL DEFAULT 0 COMMENT 'PTZ类型',
    `PTZTypeText`    varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL DEFAULT '' COMMENT 'PTZ类型描述字符串',
    `status`         tinyint(1)                                                   NOT NULL DEFAULT 1 COMMENT '设备状态（1-未激活，2-禁用，3-在线，4-离线）',
    `longitude`      double(11, 6)                                                NULL     DEFAULT NULL COMMENT '设备经度',
    `latitude`       double(11, 6)                                                NULL     DEFAULT NULL COMMENT '设备纬度',
    `streamId`       varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL DEFAULT '' COMMENT '流媒体ID',
    `subCount`       bigint(20)                                                   NOT NULL DEFAULT 0 COMMENT '子设备数',
    `parental`       tinyint(1)                                                   NOT NULL DEFAULT 1 COMMENT '是否有子设备（1-有, 0-没有）',
    `hasAudio`       tinyint(1)                                                   NOT NULL DEFAULT 1 COMMENT '是否含有音频（1-有, 0-没有）',
    `del_flag`       char(1) CHARACTER SET utf8 COLLATE utf8_general_ci           NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    `create_by`      varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL DEFAULT '' COMMENT '创建者',
    `create_time`    datetime(0)                                                  NOT NULL COMMENT '创建时间',
    `update_by`      varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci       NULL     DEFAULT '' COMMENT '更新者',
    `update_time`    datetime(0)                                                  NULL     DEFAULT NULL COMMENT '更新时间',
    `remark`         varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci      NULL     DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`, `device_sip_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 92
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '监控设备通道信息'
  ROW_FORMAT = Dynamic;

