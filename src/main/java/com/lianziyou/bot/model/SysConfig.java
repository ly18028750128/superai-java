package com.lianziyou.bot.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 系统配置
 */
@ApiModel(description = "系统配置")
@Getter
@Setter
@Accessors(chain = true)
@SuperBuilder
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_config")
public class SysConfig {

    public static final String COL_ID = "id";
    public static final String COL_REGISTRATION_METHOD = "registration_method";
    public static final String COL_DEFAULT_TIMES = "default_times";
    public static final String COL_GPT_URL = "gpt_url";
    public static final String COL_IMG_UPLOAD_URL = "img_upload_url";
    public static final String COL_IMG_RETURN_URL = "img_return_url";
    public static final String COL_API_URL = "api_url";
    public static final String COL_CLIENT_URL = "client_url";
    public static final String COL_IS_OPEN_SD = "is_open_sd";
    public static final String COL_SD_URL = "sd_url";
    public static final String COL_SD_LORA_URL = "sd_lora_url";
    public static final String COL_IS_OPEN_FLAG_STUDIO = "is_open_flag_studio";
    public static final String COL_FLAG_STUDIO_KEY = "flag_studio_key";
    public static final String COL_FLAG_STUDIO_URL = "flag_studio_url";
    public static final String COL_BAIDU_APPID = "baidu_appid";
    public static final String COL_BAIDU_SECRET = "baidu_secret";
    public static final String COL_BAIDU_KEY = "baidu_key";
    public static final String COL_BAIDU_SECRET_KEY = "baidu_secret_key";
    public static final String COL_IS_OPEN_MJ = "is_open_mj";
    public static final String COL_MJ_GUILD_ID = "mj_guild_id";
    public static final String COL_MJ_CHANNEL_ID = "mj_channel_id";
    public static final String COL_MJ_USER_TOKEN = "mj_user_token";
    public static final String COL_MJ_BOT_TOKEN = "mj_bot_token";
    public static final String COL_MJ_BOT_NAME = "mj_bot_name";
    public static final String COL_IS_OPEN_PROXY = "is_open_proxy";
    public static final String COL_PROXY_IP = "proxy_ip";
    public static final String COL_PROXY_PORT = "proxy_port";
    public static final String COL_IS_OPEN_BING = "is_open_bing";
    public static final String COL_BING_COOKIE = "bing_cookie";
    public static final String COL_IS_OPEN_STABLE_STUDIO = "is_open_stable_studio";
    public static final String COL_STABLE_STUDIO_API = "stable_studio_api";
    public static final String COL_STABLE_STUDIO_KEY = "stable_studio_key";
    public static final String COL_CLIENT_LOGO = "client_logo";
    public static final String COL_CLIENT_NAME = "client_name";
    public static final String COL_DATA_VERSION = "data_version";
    public static final String COL_DELETED = "deleted";
    public static final String COL_CREATOR = "creator";
    public static final String COL_CREATE_TIME = "create_time";
    public static final String COL_OPERATOR = "operator";
    public static final String COL_OPERATE_TIME = "operate_time";
    private static final long serialVersionUID = 326308725675949330L;
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "")
    private Long id;
    /**
     * 注册模式 1账号密码  2 短信注册 3 关闭注册 4邮件注册
     */
    @TableField(value = "registration_method")
    @ApiModelProperty(value = "注册模式 1账号密码  2 短信注册 3 关闭注册 4邮件注册")
    private Integer registrationMethod;
    /**
     * 默认注册次数
     */
    @TableField(value = "default_times")
    @ApiModelProperty(value = "默认注册次数")
    private Integer defaultTimes;
    /**
     * gpt请求地址
     */
    @TableField(value = "gpt_url")
    @ApiModelProperty(value = "gpt请求地址")
    private String gptUrl;
    /**
     * 图片上传地址
     */
    @TableField(value = "img_upload_url")
    @ApiModelProperty(value = "图片上传地址")
    private String imgUploadUrl;
    /**
     * 图片域名前缀
     */
    @TableField(value = "img_return_url")
    @ApiModelProperty(value = "图片域名前缀")
    private String imgReturnUrl;
    /**
     * 后台接口地址
     */
    @TableField(value = "api_url")
    @ApiModelProperty(value = "后台接口地址")
    private String apiUrl;
    /**
     * 客户端页面地址
     */
    @TableField(value = "client_url")
    @ApiModelProperty(value = "客户端页面地址")
    private String clientUrl;
    /**
     * 是否开启sd 0未开启 1开启
     */
    @TableField(value = "is_open_sd")
    @ApiModelProperty(value = "是否开启sd 0未开启 1开启")
    private Integer isOpenSd;
    /**
     * Sd接口地址
     */
    @TableField(value = "sd_url")
    @ApiModelProperty(value = "Sd接口地址")
    private String sdUrl;
    /**
     * sd lora地址
     */
    @TableField(value = "sd_lora_url")
    @ApiModelProperty(value = "sd lora地址")
    private String sdLoraUrl;
    /**
     * 是否开启FlagStudio 0-未开启 1开启
     */
    @TableField(value = "is_open_flag_studio")
    @ApiModelProperty(value = "是否开启FlagStudio 0-未开启 1开启")
    private Integer isOpenFlagStudio;
    /**
     * FlagStudio key
     */
    @TableField(value = "flag_studio_key")
    @ApiModelProperty(value = "FlagStudio key")
    private String flagStudioKey;
    /**
     * FlagStudio 接口地址
     */
    @TableField(value = "flag_studio_url")
    @ApiModelProperty(value = "FlagStudio 接口地址")
    private String flagStudioUrl;
    /**
     * 百度appid
     */
    @TableField(value = "baidu_appid")
    @ApiModelProperty(value = "百度appid")
    private String baiduAppid;
    /**
     * 百度Secret
     */
    @TableField(value = "baidu_secret")
    @ApiModelProperty(value = "百度Secret,")
    private String baiduSecret;
    /**
     * 百度应用key
     */
    @TableField(value = "baidu_key")
    @ApiModelProperty(value = "百度应用key")
    private String baiduKey;
    /**
     * 百度应用Secret
     */
    @TableField(value = "baidu_secret_key")
    @ApiModelProperty(value = "百度应用Secret")
    private String baiduSecretKey;
    /**
     * 是否开启mj 0未开启 1开启
     */
    @TableField(value = "is_open_mj")
    @ApiModelProperty(value = "是否开启mj 0未开启 1开启")
    private Integer isOpenMj;
    /**
     * Mj服务器id
     */
    @TableField(value = "mj_guild_id")
    @ApiModelProperty(value = "Mj服务器id")
    private String mjGuildId;
    /**
     * Mj频道id
     */
    @TableField(value = "mj_channel_id")
    @ApiModelProperty(value = "Mj频道id")
    private String mjChannelId;
    /**
     * discordtoken
     */
    @TableField(value = "mj_user_token")
    @ApiModelProperty(value = "discordtoken")
    private String mjUserToken;
    /**
     * 频道机器人token
     */
    @TableField(value = "mj_bot_token")
    @ApiModelProperty(value = "频道机器人token")
    private String mjBotToken;
    /**
     * 频道机器人名称
     */
    @TableField(value = "mj_bot_name")
    @ApiModelProperty(value = "频道机器人名称")
    private String mjBotName;
    /**
     * 是否开启代理 0关闭 1开启
     */
    @TableField(value = "is_open_proxy")
    @ApiModelProperty(value = "是否开启代理 0关闭 1开启")
    private Integer isOpenProxy;
    /**
     * 代理ip
     */
    @TableField(value = "proxy_ip")
    @ApiModelProperty(value = "代理ip")
    private String proxyIp;
    /**
     * 代理端口
     */
    @TableField(value = "proxy_port")
    @ApiModelProperty(value = "代理端口")
    private Integer proxyPort;
    /**
     * 是否开启bing 0-未开启 1开启
     */
    @TableField(value = "is_open_bing")
    @ApiModelProperty(value = "是否开启bing 0-未开启 1开启")
    private Integer isOpenBing;
    /**
     * 微软bing cookie
     */
    @TableField(value = "bing_cookie")
    @ApiModelProperty(value = "微软bing cookie")
    private String bingCookie;
    /**
     * 是否开启StableStudio 0未开启 1 开启
     */
    @TableField(value = "is_open_stable_studio")
    @ApiModelProperty(value = "是否开启StableStudio 0未开启 1 开启")
    private Integer isOpenStableStudio;
    /**
     * StableStudioapi地址前缀
     */
    @TableField(value = "stable_studio_api")
    @ApiModelProperty(value = "StableStudioapi地址前缀")
    private String stableStudioApi;
    /**
     * StableStudio key
     */
    @TableField(value = "stable_studio_key")
    @ApiModelProperty(value = "StableStudio key")
    private String stableStudioKey;
    /**
     * 客户端 logo 地址
     */
    @TableField(value = "client_logo")
    @ApiModelProperty(value = "客户端 logo 地址")
    private String clientLogo;
    /**
     * 客户端名称
     */
    @TableField(value = "client_name")
    @ApiModelProperty(value = "客户端名称")
    private String clientName;
    /**
     * 数据版本（默认为0，每次编辑+1）
     */
    @TableField(value = "data_version")
    @ApiModelProperty(value = "数据版本（默认为0，每次编辑+1）")
    private Integer dataVersion;
    /**
     * 是否删除：0-否、1-是
     */
    @TableField(value = "deleted")
    @ApiModelProperty(value = "是否删除：0-否、1-是")
    private Integer deleted;
    /**
     * 创建人编号（默认为0）
     */
    @TableField(value = "creator")
    @ApiModelProperty(value = "创建人编号（默认为0）")
    private Long creator;
    /**
     * 创建时间（默认为创建时服务器时间）
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value = "创建时间（默认为创建时服务器时间）")
    private LocalDateTime createTime;
    /**
     * 操作人编号（默认为0）
     */
    @TableField(value = "`operator`")
    @ApiModelProperty(value = "操作人编号（默认为0）")
    private Long operator;
    /**
     * 操作时间（每次更新时自动更新）
     */
    @TableField(value = "operate_time")
    @ApiModelProperty(value = "操作时间（每次更新时自动更新）")
    private LocalDateTime operateTime;
}