create table announcement
(
    id           bigint                             not null
        primary key,
    title        varchar(30)                        null comment '标题',
    content      mediumtext                         null comment '公告内容',
    sort         int      default 0                 null comment '排序',
    data_version int      default 0                 null comment '数据版本（默认为0，每次编辑+1）',
    deleted      int      default 0                 null comment '是否删除：0-否、1-是',
    creator      bigint   default 0                 null comment '创建人编号（默认为0）',
    create_time  datetime default CURRENT_TIMESTAMP null comment '创建时间（默认为创建时服务器时间）',
    operator     bigint   default 0                 null comment '操作人编号（默认为0）',
    operate_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '操作时间（每次更新时自动更新）'
)
    comment '公告';

create table card_pin
(
    id           bigint                             not null
        primary key,
    card_pin     varchar(30)                        null comment '卡密',
    number       int                                null comment '次数',
    user_id      bigint   default 0                 null comment '使用用户id',
    state        tinyint  default 0                 null comment '状态 0 未使用 1使用',
    data_version int      default 0                 null comment '数据版本（默认为0，每次编辑+1）',
    deleted      int      default 0                 null comment '是否删除：0-否、1-是',
    creator      bigint   default 0                 null comment '创建人编号（默认为0）',
    create_time  datetime default CURRENT_TIMESTAMP null comment '创建时间（默认为创建时服务器时间）',
    operator     bigint   default 0                 null comment '操作人编号（默认为0）',
    operate_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '操作时间（每次更新时自动更新）'
)
    comment '卡密表';

create table email_config
(
    id           bigint                             not null
        primary key,
    host         varchar(50)                        null comment '邮件提供商地址',
    port         int                                null comment '端口号',
    username     varchar(50)                        null comment '邮件账号',
    password     varchar(50)                        null comment 'SMTP授权密码',
    protocol     varchar(20)                        null comment '邮件协议',
    data_version int      default 0                 null comment '数据版本（默认为0，每次编辑+1）',
    deleted      int      default 0                 null comment '是否删除：0-否、1-是',
    creator      bigint   default 0                 null comment '创建人编号（默认为0）',
    create_time  datetime default CURRENT_TIMESTAMP null comment '创建时间（默认为创建时服务器时间）',
    operator     bigint   default 0                 null comment '操作人编号（默认为0）',
    operate_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '操作时间（每次更新时自动更新）'
)
    comment '微信日志';

create table error_message
(
    id            bigint                             not null
        primary key,
    user_id       bigint                             null comment '用户id',
    error_message mediumtext                         null comment '异常内容',
    url           varchar(255)                       null comment '接口地址',
    position      mediumtext                         null comment '异常位置',
    data_version  int      default 0                 null comment '数据版本（默认为0，每次编辑+1）',
    deleted       int      default 0                 null comment '是否删除：0-否、1-是',
    creator       bigint   default 0                 null comment '创建人编号（默认为0）',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间（默认为创建时服务器时间）',
    operator      bigint   default 0                 null comment '操作人编号（默认为0）',
    operate_time  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '操作时间（每次更新时自动更新）'
)
    comment '使用记录表';

create table gpt_key
(
    id           bigint                             not null
        primary key,
    `key`        varchar(100)                       null comment 'key',
    use_number   int      default 0                 null comment '使用次数',
    sort         int      default 0                 null comment '排序',
    state        int      default 0                 null comment '状态 0 启用 1禁用',
    type         tinyint  default 3                 null comment 'key类型 3-gpt3.5 4-gpt4',
    data_version int      default 0                 null comment '数据版本（默认为0，每次编辑+1）',
    deleted      int      default 0                 null comment '是否删除：0-否、1-是',
    creator      bigint   default 0                 null comment '创建人编号（默认为0）',
    create_time  datetime default CURRENT_TIMESTAMP null comment '创建时间（默认为创建时服务器时间）',
    operator     bigint   default 0                 null comment '操作人编号（默认为0）',
    operate_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '操作时间（每次更新时自动更新）'
)
    comment 'gptkey
';

create table message_log
(
    id           bigint                             not null
        primary key,
    user_id      bigint                             null comment '用户id',
    use_number   int      default 1                 null comment '使用次数',
    use_type     tinyint  default 1                 null comment '消费类型 1 次数 2 月卡',
    use_value    json                               null comment '聊天内容',
    gpt_key      varchar(100)                       null,
    send_type    tinyint  default 0                 null comment '1-gpt对话 2-gpt画图 3-sd画图 4-fs画图 5-mj画图 6-bing 7-stableStudio 8-gpt4
',
    data_version int      default 0                 null comment '数据版本（默认为0，每次编辑+1）',
    deleted      int      default 0                 null comment '是否删除：0-否、1-是',
    creator      bigint   default 0                 null comment '创建人编号（默认为0）',
    create_time  datetime default CURRENT_TIMESTAMP null comment '创建时间（默认为创建时服务器时间）',
    operator     bigint   default 0                 null comment '操作人编号（默认为0）',
    operate_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '操作时间（每次更新时自动更新）',
    init_prompt  varchar(4000)                      null comment '初始化prompt，来自t_ai_prompt',
    name         varchar(255)                       null comment '标题名称',
    tag_name     varchar(255)                       null comment '标签名称'
)
    comment '使用记录表';

create table mj_task
(
    id                  bigint                                not null
        primary key,
    user_id             bigint                                null comment '用户id',
    action              varchar(50) default '1'               null comment '任务类型',
    prompt              varchar(2000)                         null comment '关键字',
    prompt_en           varchar(2000)                         null comment '译文',
    description         varchar(2000)                         null comment '任务描述',
    state               varchar(100)                          null comment '自定义参数',
    `index`             int                                   null comment '图片位置',
    status              varchar(50)                           null comment '任务状态',
    image_url           varchar(500)                          null comment '图片地址',
    start_time          bigint                                null comment '任务开始时间',
    submit_time         bigint                                null comment '任务提交时间
',
    finish_time         bigint                                null comment '任务完成时间',
    fail_reason         varchar(100)                          null comment '任务失败原因',
    final_prompt        varchar(2000)                         null comment 'mj 任务信息',
    notify_hook         varchar(100)                          null comment '回调地址',
    related_task_id     bigint                                null comment '任务关联 id',
    message_id          varchar(20)                           null comment '消息 id',
    message_hash        varchar(100)                          null comment '消息 hash',
    progress            varchar(30)                           null comment '任务进度',
    sub_type            tinyint     default 1                 null comment '提交类型 1：web 2：公众号',
    progress_message_id varchar(50)                           null,
    flags               int         default 1                 not null comment '0',
    data_version        int         default 0                 null comment '数据版本（默认为0，每次编辑+1）',
    deleted             int         default 0                 null comment '是否删除：0-否、1-是',
    creator             bigint      default 0                 null comment '创建人编号（默认为0）',
    create_time         datetime    default CURRENT_TIMESTAMP null comment '创建时间（默认为创建时服务器时间）',
    operator            bigint      default 0                 null comment '操作人编号（默认为0）',
    operate_time        datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '操作时间（每次更新时自动更新）',
    topical_id          bigint                                null comment '主题id，关联t_use_ai_draw_topic表'
)
    comment 'Mj任务';

create table pay_config
(
    id           bigint                             not null
        primary key,
    pid          int                                null comment '易支付商户id',
    secret_key   varchar(100)                       null comment '易支付商户密钥',
    notify_url   varchar(255)                       null comment '易支付回调域名',
    return_url   varchar(255)                       null comment '易支付跳转通知地址',
    submit_url   varchar(255)                       null comment '易支付支付请求域名',
    api_url      varchar(255)                       null comment '易支付订单查询api',
    pay_type     tinyint  default 0                 null comment '支付类型 0 易支付 1卡密',
    data_version int      default 0                 null comment '数据版本（默认为0，每次编辑+1）',
    deleted      int      default 0                 null comment '是否删除：0-否、1-是',
    creator      bigint   default 0                 null comment '创建人编号（默认为0）',
    create_time  datetime default CURRENT_TIMESTAMP null comment '创建时间（默认为创建时服务器时间）',
    operator     bigint   default 0                 null comment '操作人编号（默认为0）',
    operate_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '操作时间（每次更新时自动更新）'
)
    comment '支付配置';

create table product
(
    id           bigint                             not null
        primary key,
    name         varchar(30)                        null comment '产品名',
    price        decimal(10, 2)                     null comment '价格',
    number_times int                                null comment '次数',
    sort         int      default 0                 null comment '排序',
    stock        int      default 1                 null comment '库存数',
    data_version int      default 0                 null comment '数据版本（默认为0，每次编辑+1）',
    deleted      int      default 0                 null comment '是否删除：0-否、1-是',
    creator      bigint   default 0                 null comment '创建人编号（默认为0）',
    create_time  datetime default CURRENT_TIMESTAMP null comment '创建时间（默认为创建时服务器时间）',
    operator     bigint   default 0                 null comment '操作人编号（默认为0）',
    operate_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '操作时间（每次更新时自动更新）'
)
    comment '产品表';

create table sd_lora
(
    id           bigint                             not null
        primary key,
    lora_name    varchar(50)                        null comment 'lora名',
    img_url      varchar(50)                        null comment 'lora图片地址',
    data_version int      default 0                 null comment '数据版本（默认为0，每次编辑+1）',
    deleted      int      default 0                 null comment '是否删除：0-否、1-是',
    creator      bigint   default 0                 null comment '创建人编号（默认为0）',
    create_time  datetime default CURRENT_TIMESTAMP null comment '创建时间（默认为创建时服务器时间）',
    operator     bigint   default 0                 null comment '操作人编号（默认为0）',
    operate_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '操作时间（每次更新时自动更新）'
)
    comment 'sdlora表';

create table sd_model
(
    id           bigint                             not null
        primary key,
    model_name   varchar(50)                        null comment '模型名',
    img_url      varchar(50)                        null comment '模型图片地址',
    data_version int      default 0                 null comment '数据版本（默认为0，每次编辑+1）',
    deleted      int      default 0                 null comment '是否删除：0-否、1-是',
    creator      bigint   default 0                 null comment '创建人编号（默认为0）',
    create_time  datetime default CURRENT_TIMESTAMP null comment '创建时间（默认为创建时服务器时间）',
    operator     bigint   default 0                 null comment '操作人编号（默认为0）',
    operate_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '操作时间（每次更新时自动更新）'
)
    comment 'Sd模型表';

create table sys_config
(
    id                    bigint                             not null
        primary key,
    registration_method   tinyint  default 1                 null comment '注册模式 1账号密码  2 短信注册 3 关闭注册 4邮件注册',
    default_times         int      default 10                null comment '默认注册次数',
    gpt_url               varchar(50)                        null comment 'gpt请求地址',
    img_upload_url        varchar(50)                        null comment '图片上传地址',
    img_return_url        varchar(50)                        null comment '图片域名前缀',
    api_url               varchar(50)                        null comment '后台接口地址',
    client_url            varchar(50)                        null comment '客户端页面地址',
    is_open_sd            tinyint  default 0                 null comment '是否开启sd 0未开启 1开启',
    sd_url                varchar(50)                        null comment 'Sd接口地址',
    sd_lora_url           varchar(100)                       null comment 'sd lora地址',
    is_open_flag_studio   tinyint  default 0                 null comment '是否开启FlagStudio 0-未开启 1开启',
    flag_studio_key       varchar(100)                       null comment 'FlagStudio key',
    flag_studio_url       varchar(100)                       null comment 'FlagStudio 接口地址',
    baidu_appid           varchar(100)                       null comment '百度appid',
    baidu_secret          varchar(255)                       null comment '百度Secret
',
    baidu_key             varchar(50)                        null comment '百度应用key',
    baidu_secret_key      varchar(50)                        null comment '百度应用Secret',
    is_open_mj            tinyint  default 0                 null comment '是否开启mj 0未开启 1开启',
    mj_guild_id           bigint                             null comment 'Mj服务器id',
    mj_channel_id         bigint                             null comment 'Mj频道id',
    mj_user_token         varchar(255)                       null comment 'discordtoken',
    mj_bot_token          varchar(255)                       null comment '频道机器人token',
    mj_bot_name           varchar(50)                        null comment '频道机器人名称',
    is_open_proxy         tinyint  default 0                 null comment '是否开启代理 0关闭 1开启',
    proxy_ip              varchar(20)                        null comment '代理ip',
    proxy_port            int                                null comment '代理端口',
    is_open_bing          tinyint  default 0                 null comment '是否开启bing 0-未开启 1开启',
    bing_cookie           varchar(300)                       null comment '微软bing cookie',
    is_open_stable_studio tinyint  default 0                 null comment '是否开启StableStudio 0未开启 1 开启',
    stable_studio_api     varchar(50)                        null comment 'StableStudioapi地址前缀',
    stable_studio_key     varchar(100)                       null comment 'StableStudio key',
    client_logo           varchar(50)                        null comment '客户端 logo 地址',
    client_name           varchar(50)                        null comment '客户端名称',
    data_version          int      default 0                 null comment '数据版本（默认为0，每次编辑+1）',
    deleted               int      default 0                 null comment '是否删除：0-否、1-是',
    creator               bigint   default 0                 null comment '创建人编号（默认为0）',
    create_time           datetime default CURRENT_TIMESTAMP null comment '创建时间（默认为创建时服务器时间）',
    operator              bigint   default 0                 null comment '操作人编号（默认为0）',
    operate_time          datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '操作时间（每次更新时自动更新）'
)
    comment '系统配置';

create table t_ai_prompt
(
    id           bigint auto_increment
        primary key,
    type         varchar(10)                           not null comment '类型：GPT/MJ/SD',
    source       varchar(10) default 'SYS'             not null comment '来源：SYS/FLOWGPT/',
    tag_cn       varchar(100)                          null comment '中文标签',
    tag_en       varchar(100)                          null comment '英文标签',
    description  varchar(500)                          null comment '描述',
    init_prompt  varchar(4000)                         null comment '提示语',
    creator      bigint      default 0                 not null comment '创建人编号（默认为0）',
    create_time  datetime    default CURRENT_TIMESTAMP not null comment '创建时间（默认为创建时服务器时间）',
    operator     bigint      default 0                 not null comment '操作人编号（默认为0）',
    operate_time datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '操作时间（每次更新时自动更新）',
    is_publish   tinyint     default 0                 not null comment '是否已经发布，0：未发布 1:已发布',
    constraint udx_ai_prompt
        unique (type, source, description, tag_cn)
);

create table t_ai_prompt_copy1
(
    id           bigint                                not null
        primary key,
    type         varchar(10)                           not null comment '类型：GPT/MJ/SD',
    source       varchar(10) default 'SYS'             not null comment '来源：SYS/FLOWGPT/',
    tag_cn       varchar(100)                          null comment '中文标签',
    tag_en       varchar(100)                          null comment '英文标签',
    description  varchar(500)                          null comment '描述',
    init_prompt  varchar(4000)                         null comment '提示语',
    creator      bigint      default 0                 not null comment '创建人编号（默认为0）',
    create_time  datetime    default CURRENT_TIMESTAMP not null comment '创建时间（默认为创建时服务器时间）',
    operator     bigint      default 0                 not null comment '操作人编号（默认为0）',
    operate_time datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '操作时间（每次更新时自动更新）',
    constraint udx_ai_prompt
        unique (type, source, description, tag_cn)
);

create table t_order
(
    id           bigint           not null
        primary key,
    product_id   bigint           null comment '产品id',
    user_id      bigint           null comment '购买人id',
    price        decimal(10, 2)   null comment '订单金额',
    pay_number   int    default 0 null comment '购买数量',
    state        int    default 0 null comment '状态 0待支付 1支付完成 2 支付失败',
    pay_type     varchar(10)      null comment '支付方式',
    trade_no     varchar(50)      null comment '平台订单号、卡密',
    msg          varchar(255)     null comment '支付消息',
    data_version int    default 0 null comment '数据版本（默认为0，每次编辑+1）',
    deleted      int    default 0 null comment '是否删除：0-否、1-是',
    creator      bigint default 0 null comment '创建人编号（默认为0）',
    create_time  datetime         null comment '创建时间（默认为创建时服务器时间）',
    operator     bigint default 0 null comment '操作人编号（默认为0）',
    operate_time datetime         null comment '操作时间（每次更新时自动更新）',
    charge_count int              null comment '冲值次数',
    product_name varchar(30)      null comment '产品名'
)
    comment '订单表';

create table t_user
(
    id                  bigint                                 not null
        primary key,
    name                varchar(30)                            null comment '姓名',
    mobile              varchar(30)                            null comment '手机号',
    password            varchar(100) default '123456'          null comment '密码',
    last_login_time     datetime                               null comment '上次登录时间',
    type                tinyint      default 0                 null comment '类型 0 次数用户 1 月卡用户 -1 管理员',
    remaining_times     int unsigned default '10'              null comment '剩余次数',
    from_user_name      varchar(100) default ''                not null comment '微信用户账号',
    is_event            tinyint      default 0                 null comment '是否关注公众号 0未关注 1关注',
    email               varchar(80)                            null comment 'email地址',
    ip_address          varchar(50)                            null comment '用户ip地址',
    browser_fingerprint varchar(100)                           null comment '浏览器指纹',
    avatar              varchar(255)                           null comment '头像地址',
    data_version        int          default 0                 null comment '数据版本（默认为0，每次编辑+1）',
    deleted             int          default 0                 null comment '是否删除：0-否、1-是',
    creator             bigint       default 0                 null comment '创建人编号（默认为0）',
    create_time         datetime     default CURRENT_TIMESTAMP null comment '创建时间（默认为创建时服务器时间）',
    operator            bigint       default 0                 null comment '操作人编号（默认为0）',
    operate_time        datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '操作时间（每次更新时自动更新）',
    constraint udx_user_from_user_name
        unique (from_user_name),
    constraint udx_user_mobile
        unique (mobile)
)
    comment '用户表';

create table t_user_ai_draw_topical
(
    id          bigint                             not null
        primary key,
    name        varchar(255)                       null comment '主题名称',
    type        int                                null comment '主题类型  1(Mid) 2:SD 3:Flagstudio 4:其它',
    user_id     bigint                             null comment '用户id',
    creator     bigint   default 0                 null comment '创建人编号（默认为0）',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间（默认为创建时服务器时间）'
)
    comment 'AI会话主题 ';

create table wx_log
(
    id             bigint                             not null
        primary key,
    content        mediumtext                         null comment '请求内容',
    from_user_name varchar(255)                       null comment '微信用户账号',
    data_version   int      default 0                 null comment '数据版本（默认为0，每次编辑+1）',
    deleted        int      default 0                 null comment '是否删除：0-否、1-是',
    creator        bigint   default 0                 null comment '创建人编号（默认为0）',
    create_time    datetime default CURRENT_TIMESTAMP null comment '创建时间（默认为创建时服务器时间）',
    operator       bigint   default 0                 null comment '操作人编号（默认为0）',
    operate_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '操作时间（每次更新时自动更新）'
)
    comment '微信日志';

