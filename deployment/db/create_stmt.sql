-- dev_legion
CREATE TABLE `system_seq` (
  `item_id` VARCHAR(45) NOT NULL,
  `current_num` BIGINT NULL,
  `last_num` BIGINT NULL,
  `max_num` BIGINT NULL,
  PRIMARY KEY (`item_id`));
