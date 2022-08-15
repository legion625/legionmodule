CREATE TABLE `sys_attr` (
  `uid` varchar(45) NOT NULL,
  `type_idx` tinyint DEFAULT NULL,
  `attr_key` varchar(45) DEFAULT NULL,
  `attr_value` varchar(45) DEFAULT NULL,
  `object_create_time` bigint DEFAULT NULL,
  `object_update_time` bigint DEFAULT NULL,
  PRIMARY KEY (`uid`)
) ;

CREATE TABLE `system_seq` (
  `item_id` varchar(45) NOT NULL,
  `current_num` bigint DEFAULT NULL,
  `last_num` bigint DEFAULT NULL,
  `max_num` bigint DEFAULT NULL,
  PRIMARY KEY (`item_id`)
) ;
