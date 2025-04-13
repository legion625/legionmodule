CREATE TABLE sys_attr (
    uid character varying NOT NULL,
    type_idx smallint,
    attr_key character varying,
    attr_value character varying,
    object_create_time bigint,
    object_update_time bigint,
    PRIMARY KEY (uid)
);

CREATE TABLE system_seq (
    item_id character varying NOT NULL,
    current_num bigint,
    last_num bigint,
    max_num bigint,
    PRIMARY KEY (item_id)
);
