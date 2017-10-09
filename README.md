# ag
Application Gateway
按如下步骤使用：
1.创建数据库，修改applicationContext.xml的数据库配置
2.增加表hsf_spring_consumer_bean，service和parameter_type，其建表语句如下：
  a.
  CREATE TABLE `hsf_spring_consumer_bean` (
	`id` BIGINT(20) NOT NULL,
	`interface_name` VARCHAR(128) NOT NULL COMMENT '接口类名',
	`version` VARCHAR(32) NOT NULL COMMENT '服务版本',
	`target` VARCHAR(32) NULL DEFAULT NULL COMMENT '开发环境使用，目标地址',
	`group` VARCHAR(8) NULL DEFAULT NULL,
	`gmt_created` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
	`gmt_modified` DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
	`creator` VARCHAR(32) NULL DEFAULT NULL,
	`modifier` VARCHAR(32) NULL DEFAULT NULL
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
  b.
  CREATE TABLE `service` (
	`id` BIGINT(20) NOT NULL,
	`service_name` VARCHAR(64) NOT NULL,
	`interface_name` VARCHAR(128) NOT NULL,
	`method_name` VARCHAR(32) NOT NULL,
	`rococo` TINYINT(4) NULL DEFAULT NULL,
	`return_type` VARCHAR(128) NULL DEFAULT NULL,
	`gmt_created` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
	`gmt_modified` DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
	`creator` VARCHAR(32) NULL DEFAULT NULL,
	`modifier` VARCHAR(32) NULL DEFAULT NULL
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
  c.
  CREATE TABLE `parameter_type` (
	`id` BIGINT(20) NOT NULL,
	`mid` BIGINT(20) NOT NULL,
	`position` TINYINT(4) NOT NULL,
	`type_name` VARCHAR(128) NOT NULL,
	`gmt_created` DATETIME NULL DEFAULT NULL,
	`gmt_modified` DATETIME NULL DEFAULT NULL,
	`creator` VARCHAR(32) NULL DEFAULT NULL,
	`modifier` VARCHAR(32) NULL DEFAULT NULL
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

如果您想增加支持的RPC框架，需要自行建表，编写RPC DAO扩展，对应的SQL语句，以及如何订阅服务。
