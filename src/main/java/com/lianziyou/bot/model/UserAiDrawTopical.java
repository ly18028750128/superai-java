package com.lianziyou.bot.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * AI会话主题
 */
@ApiModel(description = "AI会话主题 ")
@Data
@Accessors(chain = true)
@SuperBuilder
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_EMPTY)
@TableName(value = "t_user_ai_draw_topical")
public class UserAiDrawTopical {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "")
    private Long id;

    /**
     * 主题名称
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value = "主题名称")
    private String name;

    /**
     * 主题类型  1(Mid) 2:SD 3:Flagstudio 4:其它
     */
    @TableField(value = "`type`")
    @ApiModelProperty(value = "主题类型  1(Mid) 2:SD 3:Flagstudio 4:其它")
    private Integer type;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    @ApiModelProperty(value = "用户id")
    private Long userId;

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

    @TableField(exist = false)
    @ApiModelProperty(value = "图片地址")
    private String imageUrl;

    public static final String COL_ID = "id";

    public static final String COL_NAME = "name";

    public static final String COL_TYPE = "type";

    public static final String COL_USER_ID = "user_id";

    public static final String COL_CREATOR = "creator";

    public static final String COL_CREATE_TIME = "create_time";
}