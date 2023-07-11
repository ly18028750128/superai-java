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
 * 支付配置
 */
@ApiModel(description = "支付配置")
@Getter
@Setter
@Accessors(chain = true)
@SuperBuilder
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pay_config")
public class PayConfig {

    public static final String COL_ID = "id";
    public static final String COL_PID = "pid";
    public static final String COL_SECRET_KEY = "secret_key";
    public static final String COL_NOTIFY_URL = "notify_url";
    public static final String COL_RETURN_URL = "return_url";
    public static final String COL_SUBMIT_URL = "submit_url";
    public static final String COL_API_URL = "api_url";
    public static final String COL_PAY_TYPE = "pay_type";
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
     * 易支付商户id
     */
    @TableField(value = "pid")
    @ApiModelProperty(value = "易支付商户id")
    private Integer pid;
    /**
     * 易支付商户密钥
     */
    @TableField(value = "secret_key")
    @ApiModelProperty(value = "易支付商户密钥")
    private String secretKey;
    /**
     * 易支付回调域名
     */
    @TableField(value = "notify_url")
    @ApiModelProperty(value = "易支付回调域名")
    private String notifyUrl;
    /**
     * 易支付跳转通知地址
     */
    @TableField(value = "return_url")
    @ApiModelProperty(value = "易支付跳转通知地址")
    private String returnUrl;
    /**
     * 易支付支付请求域名
     */
    @TableField(value = "submit_url")
    @ApiModelProperty(value = "易支付支付请求域名")
    private String submitUrl;
    /**
     * 易支付订单查询api
     */
    @TableField(value = "api_url")
    @ApiModelProperty(value = "易支付订单查询api")
    private String apiUrl;
    /**
     * 支付类型 0 易支付 1卡密
     */
    @TableField(value = "pay_type")
    @ApiModelProperty(value = "支付类型 0 易支付 1卡密")
    private Integer payType;
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