CREATE TABLE `dubbo_reference_bean` (
                                      `id` BIGINT NOT NULL AUTO_INCREMENT,
                                      `bean_id` VARCHAR(32) NULL COMMENT 'spring bean id',
                                      `interface_name` VARCHAR(128) NOT NULL COMMENT '接口名称',
                                      `provider_version` VARCHAR(8) NULL COMMENT '接口版本',
                                      `check_provider_avaliable` TINYINT NOT NULL DEFAULT '0' COMMENT '启动时检查服务是否存在',
                                      `creator` VARCHAR(8) NOT NULL DEFAULT 'system',
                                      `gmt_create` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      `modifier` VARCHAR(8) NOT NULL DEFAULT 'system',
                                      `gmt_modified` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      `is_deleted` TINYINT NOT NULL DEFAULT '0',
                                      PRIMARY KEY (`id`),
                                      INDEX `idx_dubbo_reference_bean_interface_version` (`interface_name`, `provider_version`)
)
  COMMENT='dubbo rpc config'
  COLLATE='utf8_general_ci'
;
