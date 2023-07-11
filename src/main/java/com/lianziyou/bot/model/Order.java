package com.lianziyou.bot.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 订单表
 */
@ApiModel(description = "订单表")
@Getter
@Setter
@Accessors(chain = true)
@SuperBuilder
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_order")
public class Order {

    public static final String COL_ID = "id";
    public static final String COL_PRODUCT_ID = "product_id";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_PRICE = "price";
    public static final String COL_PAY_NUMBER = "pay_number";
    public static final String COL_STATE = "state";
    public static final String COL_PAY_TYPE = "pay_type";
    public static final String COL_TRADE_NO = "trade_no";
    public static final String COL_MSG = "msg";
    public static final String COL_DATA_VERSION = "data_version";
    public static final String COL_DELETED = "deleted";
    public static final String COL_CREATOR = "creator";
    public static final String COL_CREATE_TIME = "create_time";
    public static final String COL_OPERATOR = "operator";
    public static final String COL_OPERATE_TIME = "operate_time";
    public static final String COL_CHARGE_COUNT = "charge_count";
    public static final String COL_PRODUCT_NAME = "product_name";
    private static final long serialVersionUID = 326308725675949330L;
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "")
    private Long id;
    /**
     * 产品id
     */
    @TableField(value = "product_id")
    @ApiModelProperty(value = "产品id")
    private Long productId;
    /**
     * 购买人id
     */
    @TableField(value = "user_id")
    @ApiModelProperty(value = "购买人id")
    private Long userId;
    /**
     * 订单金额
     */
    @TableField(value = "price")
    @ApiModelProperty(value = "订单金额")
    private BigDecimal price;
    /**
     * 购买数量
     */
    @TableField(value = "pay_number")
    @ApiModelProperty(value = "购买数量")
    private Integer payNumber;
    /**
     * 状态 0待支付 1支付完成 2 支付失败
     */
    @TableField(value = "`state`")
    @ApiModelProperty(value = "状态 0待支付 1支付完成 2 支付失败")
    private Integer state;
    /**
     * 支付方式
     */
    @TableField(value = "pay_type")
    @ApiModelProperty(value = "支付方式")
    private String payType;
    /**
     * 平台订单号、卡密
     */
    @TableField(value = "trade_no")
    @ApiModelProperty(value = "平台订单号、卡密")
    private String tradeNo;
    /**
     * 支付消息
     */
    @TableField(value = "msg")
    @ApiModelProperty(value = "支付消息")
    private String msg;
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
    /**
     * 冲值次数
     */
    @TableField(value = "charge_count")
    @ApiModelProperty(value = "冲值次数")
    private Integer chargeCount;
    /**
     * 产品名
     */
    @TableField(value = "product_name")
    @ApiModelProperty(value = "产品名")
    private String productName;
}