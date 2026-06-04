-- ----------------------------
-- ai_knowledge 表新增 store_id 字段，关联向量库
-- ----------------------------
ALTER TABLE ai_knowledge ADD COLUMN store_id BIGINT(20) DEFAULT NULL COMMENT '关联的向量库ID' AFTER user_id;
