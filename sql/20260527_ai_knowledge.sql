-- ----------------------------
-- AI知识库表
-- ----------------------------
drop table if exists ai_knowledge;
create table ai_knowledge (
  knowledge_id     bigint(20)      not null auto_increment    comment '知识库ID',
  knowledge_name   varchar(100)    not null                   comment '知识库名称',
  description      varchar(500)    default ''                 comment '知识库描述',
  user_id          bigint(20)      not null                   comment '创建用户ID',
  file_count       int(11)         default 0                  comment '文件数量',
  status           char(1)         default '0'                comment '状态（0正常 1停用）',
  del_flag         char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by        varchar(64)     default ''                 comment '创建者',
  create_time      datetime                                   comment '创建时间',
  update_by        varchar(64)     default ''                 comment '更新者',
  update_time      datetime                                   comment '更新时间',
  remark           varchar(500)    default null               comment '备注',
  primary key (knowledge_id)
) engine=innodb auto_increment=1 comment = 'AI知识库表';

-- ----------------------------
-- AI知识库文件表
-- ----------------------------
drop table if exists ai_knowledge_file;
create table ai_knowledge_file (
  file_id          bigint(20)      not null auto_increment    comment '文件ID',
  knowledge_id     bigint(20)      not null                   comment '知识库ID',
  file_name        varchar(200)    not null                   comment '文件名称',
  file_path        varchar(500)    default ''                 comment '文件存储路径',
  file_size        bigint(20)      default 0                  comment '文件大小(字节)',
  file_type        varchar(50)     default ''                 comment '文件类型',
  content          longtext                                   comment '文件提取的文本内容',
  status           char(1)         default '0'                comment '状态（0正常 1失败）',
  create_by        varchar(64)     default ''                 comment '创建者',
  create_time      datetime                                   comment '创建时间',
  primary key (file_id),
  key idx_knowledge_id (knowledge_id)
) engine=innodb auto_increment=1 comment = 'AI知识库文件表';

-- ----------------------------
-- AI知识库文件分块表
-- ----------------------------
drop table if exists ai_knowledge_chunk;
create table ai_knowledge_chunk (
  chunk_id         bigint(20)      not null auto_increment    comment '分块ID',
  file_id          bigint(20)      not null                   comment '文件ID',
  knowledge_id     bigint(20)      not null                   comment '知识库ID',
  content          text                                       comment '文本块内容',
  embedding        text                                       comment '向量数据(JSON数组)',
  chunk_index      int(11)         default 0                  comment '分块序号',
  status           char(1)         default '0'                comment '状态（0正常 1失败）',
  create_time      datetime                                   comment '创建时间',
  primary key (chunk_id),
  key idx_file_id (file_id),
  key idx_knowledge_id (knowledge_id)
) engine=innodb auto_increment=1 comment = 'AI知识库文件分块表';
