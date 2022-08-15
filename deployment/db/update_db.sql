
-- 0.7.1 -> 0.10.0
CREATE TABLE `system_seq` (
  `item_id` VARCHAR(45) NOT NULL,
  `current_num` BIGINT NULL,
  `last_num` BIGINT NULL,
  `max_num` BIGINT NULL,
  PRIMARY KEY (`item_id`));


-- 0.10.1 -> 0.10.2
CREATE TABLE `sys_attr` (
  `uid` VARCHAR(45) NOT NULL,
  `type_idx` TINYINT NULL,
  `attr_key` VARCHAR(45) NULL,
  `attr_value` VARCHAR(45) NULL,
  `object_create_time` BIGINT NOT NULL,
  `object_update_time` BIGINT NOT NULL,
  PRIMARY KEY (`uid`));
-- dev_legion_local
