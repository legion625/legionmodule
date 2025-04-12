CREATE TABLE sys_attr (
  uid VARCHAR NOT NULL,
  type_idx SMALLINT,
  attr_key VARCHAR,
  attr_value VARCHAR,
  object_create_time BIGINT,
  object_update_time BIGINT,
  PRIMARY KEY (uid)
);

CREATE TABLE system_seq (
  item_id VARCHAR NOT NULL,
  current_num BIGINT,
  last_num BIGINT,
  max_num BIGINT,
  PRIMARY KEY (item_id)
);
