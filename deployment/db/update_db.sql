
-- 0.7.1 -> 0.10.0
CREATE TABLE `system_seq` (
  `item_id` VARCHAR(45) NOT NULL,
  `current_num` BIGINT NULL,
  `last_num` BIGINT NULL,
  `max_num` BIGINT NULL,
  PRIMARY KEY (`item_id`));
-- dev_legion_local

-- staging
CREATE TABLE `sys_attr` (
  `uid` VARCHAR(45) NOT NULL,
  `type_idx` TINYINT NULL,
  `key` VARCHAR(45) NULL,
  `value` VARCHAR(45) NULL,
  `object_create_time` BIGINT NOT NULL,
  `object_update_time` BIGINT NOT NULL,
  PRIMARY KEY (`uid`));
