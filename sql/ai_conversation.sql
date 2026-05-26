-- ----------------------------
-- AI问答对话表
-- ----------------------------
drop table if exists ai_conversation;
create table ai_conversation (
  conversation_id   bigint(20)      not null auto_increment    comment '对话ID',
  title             varchar(200)    default ''                 comment '对话标题',
  user_id           bigint(20)      not null                   comment '用户ID',
  model             varchar(100)    default ''                 comment '使用的模型',
  message_count     int(11)         default 0                  comment '消息数量',
  status            char(1)         default '0'                comment '对话状态（0进行中 1已结束）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (conversation_id)
) engine=innodb auto_increment=1 comment = 'AI问答对话表';

-- ----------------------------
-- AI问答消息表
-- ----------------------------
drop table if exists ai_message;
create table ai_message (
  message_id        bigint(20)      not null auto_increment    comment '消息ID',
  conversation_id   bigint(20)      not null                   comment '对话ID',
  role              varchar(20)     not null                   comment '角色（user用户 assistant助手）',
  content           text                                       comment '消息内容',
  tokens            int(11)         default 0                  comment 'token消耗量',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  primary key (message_id),
  key idx_conversation_id (conversation_id)
) engine=innodb auto_increment=1 comment = 'AI问答消息表';
