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
 * 用户表
 */
@ApiModel(description = "用户表")
@Getter
@Setter
@Accessors(chain = true)
@SuperBuilder
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_user")
public class User {

    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_MOBILE = "mobile";
    public static final String COL_PASSWORD = "password";
    public static final String COL_LAST_LOGIN_TIME = "last_login_time";
    public static final String COL_TYPE = "type";
    public static final String COL_REMAINING_TIMES = "remaining_times";
    public static final String COL_FROM_USER_NAME = "from_user_name";
    public static final String COL_IS_EVENT = "is_event";
    public static final String COL_EMAIL = "email";
    public static final String COL_IP_ADDRESS = "ip_address";
    public static final String COL_BROWSER_FINGERPRINT = "browser_fingerprint";
    public static final String COL_AVATAR = "avatar";
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
     * 姓名
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value = "姓名")
    private String name;
    /**
     * 手机号
     */
    @TableField(value = "mobile")
    @ApiModelProperty(value = "手机号")
    private String mobile;
    /**
     * 密码
     */
    @TableField(value = "`password`")
    @ApiModelProperty(value = "密码")
    private String password;
    /**
     * 上次登录时间
     */
    @TableField(value = "last_login_time")
    @ApiModelProperty(value = "上次登录时间")
    private LocalDateTime lastLoginTime;
    /**
     * 类型 0 次数用户 1 月卡用户 -1 管理员
     */
    @TableField(value = "`type`")
    @ApiModelProperty(value = "类型 0 次数用户 1 月卡用户 -1 管理员")
    private Integer type;
    /**
     * 剩余次数
     */
    @TableField(value = "remaining_times")
    @ApiModelProperty(value = "剩余次数")
    private Integer remainingTimes;
    /**
     * 微信用户账号
     */
    @TableField(value = "from_user_name")
    @ApiModelProperty(value = "微信用户账号")
    private String fromUserName;
    /**
     * 是否关注公众号 0未关注 1关注
     */
    @TableField(value = "is_event")
    @ApiModelProperty(value = "是否关注公众号 0未关注 1关注")
    private Integer isEvent;
    /**
     * email地址
     */
    @TableField(value = "email")
    @ApiModelProperty(value = "email地址")
    private String email;
    /**
     * 用户ip地址
     */
    @TableField(value = "ip_address")
    @ApiModelProperty(value = "用户ip地址")
    private String ipAddress;
    /**
     * 浏览器指纹
     */
    @TableField(value = "browser_fingerprint")
    @ApiModelProperty(value = "浏览器指纹")
    private String browserFingerprint;
    /**
     * 头像地址
     */
    @TableField(value = "avatar")
    @ApiModelProperty(value = "头像地址")
    private String avatar;
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