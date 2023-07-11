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
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 使用记录表
 */
@ApiModel(description = "使用记录表")
@Data
@Accessors(chain = true)
@SuperBuilder
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "message_log")
public class MessageLog {

    private static final long serialVersionUID = 326308725675949330L;
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "")
    private Long id;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    @ApiModelProperty(value = "用户id")
    private Long userId;

    /**
     * 使用次数
     */
    @TableField(value = "use_number")
    @ApiModelProperty(value = "使用次数")
    private Integer useNumber;

    /**
     * 消费类型 1 次数 2 月卡
     */
    @TableField(value = "use_type")
    @ApiModelProperty(value = "消费类型 1 次数 2 月卡")
    private Integer useType;

    /**
     * 聊天内容
     */
    @TableField(value = "use_value")
    @ApiModelProperty(value = "聊天内容")
    private String useValue;

    @TableField(value = "gpt_key")
    @ApiModelProperty(value = "")
    private String gptKey;

    /**
     * 1-gpt对话 2-gpt画图 3-sd画图 4-fs画图 5-mj画图 6-bing 7-stableStudio 8-gpt4
     */
    @TableField(value = "send_type")
    @ApiModelProperty(value = "1-gpt对话 2-gpt画图 3-sd画图 4-fs画图 5-mj画图 6-bing 7-stableStudio 8-gpt4,")
    private Integer sendType;

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
     * 初始化prompt，来自t_ai_prompt
     */
    @TableField(value = "init_prompt")
    @ApiModelProperty(value = "初始化prompt，来自t_ai_prompt")
    private String initPrompt;

    /**
     * 标题名称
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value = "标题名称")
    private String name;

    /**
     * 标签名称
     */
    @TableField(value = "tag_name")
    @ApiModelProperty(value = "标签名称")
    private String tagName;

    public static final String COL_ID = "id";

    public static final String COL_USER_ID = "user_id";

    public static final String COL_USE_NUMBER = "use_number";

    public static final String COL_USE_TYPE = "use_type";

    public static final String COL_USE_VALUE = "use_value";

    public static final String COL_GPT_KEY = "gpt_key";

    public static final String COL_SEND_TYPE = "send_type";

    public static final String COL_DATA_VERSION = "data_version";

    public static final String COL_DELETED = "deleted";

    public static final String COL_CREATOR = "creator";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_OPERATOR = "operator";

    public static final String COL_OPERATE_TIME = "operate_time";

    public static final String COL_INIT_PROMPT = "init_prompt";

    public static final String COL_NAME = "name";

    public static final String COL_TAG_NAME = "tag_name";
    @TableField(exist = false)
    @ApiModelProperty(value = "提示语ID,关联ai_prompt表")
    private Long initPromptId;
}