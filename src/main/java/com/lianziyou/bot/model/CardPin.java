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
 * 卡密表
 */
@ApiModel(description = "卡密表")
@Getter
@Setter
@Accessors(chain = true)
@SuperBuilder
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "card_pin")
public class CardPin {

    public static final String COL_ID = "id";
    public static final String COL_CARD_PIN = "card_pin";
    public static final String COL_NUMBER = "number";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_STATE = "state";
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
     * 卡密
     */
    @TableField(value = "card_pin")
    @ApiModelProperty(value = "卡密")
    private String cardPin;
    /**
     * 次数
     */
    @TableField(value = "`number`")
    @ApiModelProperty(value = "次数")
    private Integer number;
    /**
     * 使用用户id
     */
    @TableField(value = "user_id")
    @ApiModelProperty(value = "使用用户id")
    private Long userId;
    /**
     * 状态 0 未使用 1使用
     */
    @TableField(value = "`state`")
    @ApiModelProperty(value = "状态 0 未使用 1使用")
    private Integer state;
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