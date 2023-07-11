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
 * 微信日志
 */
@ApiModel(description = "微信日志")
@Getter
@Setter
@Accessors(chain = true)
@SuperBuilder
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "wx_log")
public class WxLog {

    public static final String COL_ID = "id";
    public static final String COL_CONTENT = "content";
    public static final String COL_FROM_USER_NAME = "from_user_name";
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
     * 请求内容
     */
    @TableField(value = "content")
    @ApiModelProperty(value = "请求内容")
    private String content;
    /**
     * 微信用户账号
     */
    @TableField(value = "from_user_name")
    @ApiModelProperty(value = "微信用户账号")
    private String fromUserName;
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