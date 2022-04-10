CREATE TABLE `doc_file` (
  `uid` varchar(45) COLLATE utf8_bin NOT NULL,
  `path` varchar(200) COLLATE utf8_bin NOT NULL,
  `file_name` varchar(45) COLLATE utf8_bin NOT NULL,
  `object_create_time` varchar(45) COLLATE utf8_bin NOT NULL,
  `object_update_time` varchar(45) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`uid`)
) ;

CREATE TABLE `obj_seq` (
  `obj_key` varchar(45) NOT NULL,
  `obj_index` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`obj_key`)
) ;

CREATE TABLE `system_seq` (
  `item_id` varchar(45) COLLATE utf8_bin NOT NULL,
  `current_num` bigint(20) DEFAULT NULL,
  `last_num` bigint(20) DEFAULT NULL,
  `max_num` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`item_id`)
) ;
