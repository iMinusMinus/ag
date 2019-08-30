CREATE TABLE `service` (
                         `id` BIGINT NOT NULL AUTO_INCREMENT,
                         `service_name` VARCHAR(64) NOT NULL COMMENT '服务API名称',
                         `interface_name` VARCHAR(128) NOT NULL COMMENT '接口名',
                         `method_name` VARCHAR(32) NOT NULL COMMENT '方法名',
                         `return_type` VARCHAR(128) NOT NULL COMMENT '方法返回类型',
                         `rococo` TINYINT NOT NULL DEFAULT '0' COMMENT '0,无;1,身份认证;2,权限认证;4,验签;8,解密',
                         `generic` TINYINT NOT NULL COMMENT '参数泛型:0,无;1,入参;2,出参;4,入参和出参',
                         `creator` VARCHAR(8) NULL,
                         `gmt_created` TIMESTAMP NULL,
                         `modifier` VARCHAR(8) NULL,
                         `gmt_modified` TIMESTAMP NULL,
                         `is_deleted` TINYINT NULL DEFAULT '0',
                         PRIMARY KEY (`id`),
                         INDEX `idx_service_service_name` (`service_name`)
)
  COMMENT='rpc service'
  COLLATE='utf8_general_ci'
;

CREATE TABLE `parameter_type` (
                                `id` BIGINT NOT NULL AUTO_INCREMENT,
                                `interface_name` VARCHAR(128) NOT NULL COMMENT '接口名',
                                `method_name` VARCHAR(32) NOT NULL COMMENT '方法名',
                                `position` TINYINT NOT NULL DEFAULT '0' COMMENT '参数位置',
                                `type_name` VARCHAR(128) NOT NULL COMMENT '参数类型',
                                `creator` VARCHAR(8) NULL DEFAULT 'system',
                                `modifier` VARCHAR(8) NULL DEFAULT 'system',
                                `gmt_created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                `gmt_modified` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                `is_deleted` TINYINT NOT NULL DEFAULT '0',
                                PRIMARY KEY (`id`),
                                INDEX `idx_parameter_type_interface_name_method_name` (`interface_name`, `method_name`)
)
  COMMENT='参数信息'
  COLLATE='utf8_general_ci'
;
