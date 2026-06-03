-- ----------------------------
-- 向量库表 vb_store
-- ----------------------------
DROP TABLE IF EXISTS vb_store;
CREATE TABLE vb_store (
  store_id      BIGINT(20)    NOT NULL AUTO_INCREMENT  COMMENT '向量库ID',
  store_name    VARCHAR(100)  NOT NULL                 COMMENT '向量库名称',
  description   VARCHAR(500)  DEFAULT ''               COMMENT '描述',
  dimension     INT(11)       NOT NULL DEFAULT 1536    COMMENT '向量维度',
  vector_count  INT(11)       NOT NULL DEFAULT 0       COMMENT '向量数量',
  status        CHAR(1)       NOT NULL DEFAULT '0'     COMMENT '状态（0正常 1停用）',
  del_flag      CHAR(1)       NOT NULL DEFAULT '0'     COMMENT '删除标志（0代表存在 2代表删除）',
  create_by     VARCHAR(64)   DEFAULT ''               COMMENT '创建者',
  create_time   DATETIME      DEFAULT NULL             COMMENT '创建时间',
  update_by     VARCHAR(64)   DEFAULT ''               COMMENT '更新者',
  update_time   DATETIME      DEFAULT NULL             COMMENT '更新时间',
  remark        VARCHAR(500)  DEFAULT NULL             COMMENT '备注',
  PRIMARY KEY (store_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='向量库表';

-- ----------------------------
-- 向量数据表 vb_vector_data
-- ----------------------------
DROP TABLE IF EXISTS vb_vector_data;
CREATE TABLE vb_vector_data (
  data_id       BIGINT(20)    NOT NULL AUTO_INCREMENT  COMMENT '数据ID',
  store_id      BIGINT(20)    NOT NULL                 COMMENT '所属向量库ID',
  content       TEXT                                   COMMENT '文本内容',
  embedding     TEXT                                   COMMENT '向量数据(JSON数组)',
  metadata      TEXT                                   COMMENT '元数据(JSON)',
  status        CHAR(1)       NOT NULL DEFAULT '0'     COMMENT '状态（0正常 1停用）',
  del_flag      CHAR(1)       NOT NULL DEFAULT '0'     COMMENT '删除标志（0代表存在 2代表删除）',
  create_by     VARCHAR(64)   DEFAULT ''               COMMENT '创建者',
  create_time   DATETIME      DEFAULT NULL             COMMENT '创建时间',
  update_by     VARCHAR(64)   DEFAULT ''               COMMENT '更新者',
  update_time   DATETIME      DEFAULT NULL             COMMENT '更新时间',
  remark        VARCHAR(500)  DEFAULT NULL             COMMENT '备注',
  PRIMARY KEY (data_id),
  KEY idx_store_id (store_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='向量数据表';
