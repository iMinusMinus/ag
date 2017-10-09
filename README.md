# ag
Application Gateway
按如下步骤使用：
1. 创建数据库，修改applicationContext.xml的数据库配置
2. 增加表hsf_spring_consumer_bean，service和parameter_type，其建表语句如下：
  * CREATE TABLE `hsf_spring_consumer_bean` (
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
   * CREATE TABLE `service` (
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
  * CREATE TABLE `parameter_type` (
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
3. HSF依赖插件，请获取。不能获取的请去除HSF相关配置，并添加自定义的RPC框架。

如果您想增加支持的RPC框架：
1. 修改pom.xml，增加删除依赖
2. 扩展HttpDispatcherService和/或RpcDispatcherService
3. 扩展RpcConfigDao，编写sql
4. 创建对应的表
5. 实现Subscriber
6. 更新applicationContext.xml中的spring配置
