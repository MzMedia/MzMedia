drop table if exists protocol;
create table protocol
(
    tenant_id        varchar(20)          default '000000' comment '租户编号',
    id               bigint(20)   not null comment 'ID',
    name             varchar(30)  not null comment '协议名称',
    jar_file         varchar(256) not null comment 'jar包文件',
    codec_file       varchar(256) not null comment '编码脚本文件',
    network          varchar(30) not null comment '协议网络',
    config           varchar(30) not null comment '协议配置',
    codec            varchar(30) not null comment '编解码器',
    codec_type       varchar(30) not null comment '编解码类型',
    script           varchar(30) not null comment '脚本内容',
    script_lang      varchar(30) not null comment '脚本语言',
    del_flag         char(1)              default '0' comment '删除标志（0代表存在 2代表删除）',
    create_dept      bigint(20)           default null comment '创建部门',
    create_by        bigint(20)           default null comment '创建者',
    create_time      datetime comment '创建时间',
    update_by        bigint(20)           default null comment '更新者',
    update_time      datetime comment '更新时间',
    remark           varchar(500)         default null comment '备注',
    primary key (id)
) engine = innodb comment = '协议表';

drop table if exists protocol_codec;
create table protocol_codec
(
    `tenant_id`        varchar(20)          default '000000' comment '租户编号',
    `id`              bigint(20)   not null comment 'ID',
    `name`             varchar(30)  not null comment '协议编解码名称',
    `describe`         varchar(256)  not null comment '协议编解码描述',
    `script`           varchar(30) not null comment '脚本内容',
    `script_lang`      varchar(30) not null comment '脚本语言',
    `del_flag`         char(1)              default '0' comment '删除标志（0代表存在 2代表删除）',
    `create_dept`      bigint(20)           default null comment '创建部门',
    `create_by`        bigint(20)           default null comment '创建者',
    `create_time`      datetime comment '创建时间',
    `update_by`        bigint(20)           default null comment '更新者',
    `update_time`      datetime comment '更新时间',
    `remark`           varchar(500)         default null comment '备注',
    primary key (id)
) engine = innodb comment = '协议编解码器表';
