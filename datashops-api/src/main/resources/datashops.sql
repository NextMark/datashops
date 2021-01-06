CREATE DATABASE IF NOT EXISTS datashops;

USE datashops;

-- user
CREATE TABLE `t_user`
(
    `id`              bigint(20) unsigned                              NOT NULL AUTO_INCREMENT COMMENT '用户Id',
    `email`           varchar(64) CHARACTER SET utf8 COLLATE utf8_bin  NOT NULL COMMENT '邮箱',
    `name`            varchar(32) CHARACTER SET utf8 COLLATE utf8_bin  NOT NULL COMMENT '用户名',
    `password`        varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '密码',
    `avatar`          varchar(1024)                                             DEFAULT NULL COMMENT '头像',
    `phone`           varchar(16)                                               DEFAULT NULL COMMENT '电话',
    `last_login_time` TIMESTAMP                                                 DEFAULT NULL COMMENT '上一次登录时间',
    `create_time`     TIMESTAMP                                        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录的创建时间',
    `update_time`     TIMESTAMP                                        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录的更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `ix_user_username` (`name`),
    UNIQUE KEY `ix_user_email` (`email`),
    UNIQUE KEY `ix_user_phone` (`phone`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
    COMMENT ='用户表';

CREATE TABLE `t_user_permission`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Id',
    `uid`         bigint(10)          NOT NULL COMMENT 'uid',
    `role_id`     bigint(10)          NOT NULL COMMENT 'role_id',
    `create_time` TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录的创建时间',
    `update_time` TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录的更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
    COMMENT ='权限表';

CREATE TABLE `t_user_menu`
(
    `id`          bigint(20) unsigned                              NOT NULL AUTO_INCREMENT COMMENT 'Id',
    `path`        varchar(64) CHARACTER SET utf8 COLLATE utf8_bin  NOT NULL COMMENT '路径',
    `name`        varchar(64) CHARACTER SET utf8 COLLATE utf8_bin  NOT NULL COMMENT '名称',
    `meta`        varchar(512) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'meta',
    `parent_id`   int                                              NOT NULL COMMENT 'parent id',
    `hidden`      tinyint(1)                                       NOT NULL DEFAULT '0',
    `sort`        int(11)                                          NOT NULL DEFAULT '10',
    `authority`   varchar(128) CHARACTER SET utf8 COLLATE utf8_bin not null comment '',
    `parameters`  varchar(128) CHARACTER SET utf8 COLLATE utf8_bin not null comment '',
    `component`   varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '组件',
    `create_time` TIMESTAMP                                        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录的创建时间',
    `update_time` TIMESTAMP                                        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录的更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
    COMMENT ='菜单表';

CREATE TABLE `t_user_role`
(
    `id`          bigint(20) unsigned                             NOT NULL AUTO_INCREMENT COMMENT 'Id',
    `name`        varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '名称',
    `create_time` TIMESTAMP                                       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录的创建时间',
    `update_time` TIMESTAMP                                       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录的更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
    COMMENT ='角色表';

CREATE TABLE `t_user_role_permission`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Id',
    `role_id`     bigint(10)          NOT NULL COMMENT 'role id',
    `menu_id`     bigint(10)          NOT NULL COMMENT 'menu_id',
    `create_time` TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录的创建时间',
    `update_time` TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录的更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `ix_rid_mid` (`role_id`, `menu_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
    COMMENT ='角色权限表';

-- job
CREATE TABLE IF NOT EXISTS `job`
(
    `id`               BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `name`             VARCHAR(255)       NOT NULL COMMENT '作业名称',
    `description`      VARCHAR(1024)      NOT NULL DEFAULT '' COMMENT '作业描述',
    `type`             TINYINT(4)         NOT NULL COMMENT '作业类型',
    `client_version`   VARCHAR(20)        NOT NULL DEFAULT '' COMMENT '客户端版本',
    `config_json`      MEDIUMTEXT         NULL COMMENT '作业配置',
    `last_instance_id` BIGINT             NULL COMMENT '最新实例的ID',
    `last_status`      TINYINT(4)         NULL COMMENT '最新实例的状态',
    `last_app_id`      VARCHAR(100)       NULL COMMENT '最新实例的app_id',
    `last_start_time`  TIMESTAMP          NULL COMMENT '最新实例的开始时间',
    `last_stop_time`   TIMESTAMP          NULL COMMENT '最新实例的结束时间',
    `create_time`      TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录的创建时间',
    `update_time`      TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录的更新时间',
    UNIQUE KEY `uniq_name` (`name`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='作业表';

-- job_instance
CREATE TABLE IF NOT EXISTS `job_instance`
(
    `id`          BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `job_id`      BIGINT             NOT NULL COMMENT '作业的ID, 即 job 表的 id',
    `config_json` MEDIUMTEXT         NULL COMMENT '实例启动时的镜像配置',
    `status`      TINYINT(4)         NOT NULL COMMENT '实例的状态',
    `app_id`      VARCHAR(100)       NOT NULL DEFAULT '' COMMENT '实例的集群任务id',
    `start_time`  TIMESTAMP          NULL COMMENT '实例的开始时间',
    `stop_time`   TIMESTAMP          NULL COMMENT '实例的结束时间',
    `create_time` TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录的创建时间',
    `update_time` TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录的更新时间',
    KEY `idx_job_id` (`job_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='作业实例表';
