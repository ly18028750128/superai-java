package com.lianziyou.bot.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lianziyou.bot.constant.AiPromptConst.PromptSource;
import com.lianziyou.bot.constant.AiPromptConst.PromptType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@ApiModel(description = "t_ai_prompt")
@Data
@Accessors(chain = true)
@SuperBuilder
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_ai_prompt")
public class AiPrompt {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "")
    private Long id;

    /**
     * 类型：GPT/MJ/SD
     */
    @TableField(value = "`type`")
    @ApiModelProperty(value = "类型：GPT/MJ/SD")
    private PromptType type;

    /**
     * 来源：SYS/FLOWGPT/
     */
    @TableField(value = "`source`")
    @ApiModelProperty(value = "来源：SYS/FLOWGPT/")
    private PromptSource source;

    /**
     * 中文标签
     */
    @TableField(value = "tag_cn")
    @ApiModelProperty(value = "中文标签")
    private String tagCn;

    /**
     * 英文标签
     */
    @TableField(value = "tag_en")
    @ApiModelProperty(value = "英文标签")
    private String tagEn;

    /**
     * 描述
     */
    @TableField(value = "description")
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 提示语
     */
    @TableField(value = "init_prompt")
    @ApiModelProperty(value = "提示语")
    private String initPrompt;

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
     * 是否已经发布，0：未发布 1:已发布
     */
    @TableField(value = "is_publish")
    @ApiModelProperty(value = "是否已经发布，0：未发布 1:已发布")
    private Integer isPublish;

    public static final String COL_ID = "id";

    public static final String COL_TYPE = "type";

    public static final String COL_SOURCE = "source";

    public static final String COL_TAG_CN = "tag_cn";

    public static final String COL_TAG_EN = "tag_en";

    public static final String COL_DESCRIPTION = "description";

    public static final String COL_INIT_PROMPT = "init_prompt";

    public static final String COL_CREATOR = "creator";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_OPERATOR = "operator";

    public static final String COL_OPERATE_TIME = "operate_time";

    public static final String COL_IS_PUBLISH = "is_publish";
}