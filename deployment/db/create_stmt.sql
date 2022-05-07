CREATE TABLE `doc_file` (
  `uid` varchar(45) NOT NULL,
  `path` varchar(200) DEFAULT NULL,
  `file_name` varchar(45) DEFAULT NULL,
  `object_create_time` varchar(45) DEFAULT NULL,
  `object_update_time` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`uid`)
) ;

CREATE TABLE `obj_seq` (
  `obj_key` varchar(45) NOT NULL,
  `obj_index` bigint DEFAULT NULL,
  PRIMARY KEY (`obj_key`)
) ;

CREATE TABLE `sys_attr` (
  `uid` varchar(45) NOT NULL,
  `type_idx` tinyint DEFAULT NULL,
  `key` varchar(45) DEFAULT NULL,
  `value` varchar(45) DEFAULT NULL,
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
