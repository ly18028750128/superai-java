package com.lianziyou.bot.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lianziyou.bot.enums.mj.TaskAction;
import com.lianziyou.bot.enums.mj.TaskStatus;
import com.lianziyou.bot.utils.sys.FileUtil;
import com.unknow.first.mongo.utils.MongoDBUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

/**
 * Mj任务
 */
@ApiModel(description = "Mj任务")
@Getter
@Setter
@Accessors(chain = true)
@SuperBuilder
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "mj_task")
@Slf4j
public class MjTask {

    public static final String COL_ID = "id";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_ACTION = "action";
    public static final String COL_PROMPT = "prompt";
    public static final String COL_PROMPT_EN = "prompt_en";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_STATE = "state";
    public static final String COL_INDEX = "index";
    public static final String COL_STATUS = "status";
    public static final String COL_IMAGE_URL = "image_url";
    public static final String COL_START_TIME = "start_time";
    public static final String COL_SUBMIT_TIME = "submit_time";
    public static final String COL_FINISH_TIME = "finish_time";
    public static final String COL_FAIL_REASON = "fail_reason";
    public static final String COL_FINAL_PROMPT = "final_prompt";
    public static final String COL_NOTIFY_HOOK = "notify_hook";
    public static final String COL_RELATED_TASK_ID = "related_task_id";
    public static final String COL_MESSAGE_ID = "message_id";
    public static final String COL_MESSAGE_HASH = "message_hash";
    public static final String COL_PROGRESS = "progress";
    public static final String COL_DATA_VERSION = "data_version";
    public static final String COL_DELETED = "deleted";
    public static final String COL_CREATOR = "creator";
    public static final String COL_CREATE_TIME = "create_time";
    public static final String COL_OPERATOR = "operator";
    public static final String COL_OPERATE_TIME = "operate_time";
    private static final long serialVersionUID = 326308725675949330L;
    @JsonIgnore
    private final transient Object lock = new Object();
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
     * 关键字
     */
    @TableField(value = "prompt")
    @ApiModelProperty(value = "关键字")
    private String prompt;
    /**
     * 译文
     */
    @TableField(value = "prompt_en")
    @ApiModelProperty(value = "译文")
    private String promptEn;
    /**
     * 任务描述
     */
    @TableField(value = "description")
    @ApiModelProperty(value = "任务描述")
    private String description;
    /**
     * 自定义参数
     */
    @TableField(value = "`state`")
    @ApiModelProperty(value = "自定义参数")
    private String state;
    /**
     * 图片位置
     */
    @TableField(value = "`index`")
    @ApiModelProperty(value = "图片位置")
    private Integer index;
    /**
     * 任务状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value = "任务状态")
    private TaskStatus status;
    /**
     * 图片地址
     */
    @TableField(value = "image_url")
    @ApiModelProperty(value = "图片地址")
    private String imageUrl;
    /**
     * 任务开始时间
     */
    @TableField(value = "start_time")
    @ApiModelProperty(value = "任务开始时间")
    private Long startTime;
    /**
     * 任务提交时间
     */
    @TableField(value = "submit_time")
    @ApiModelProperty(value = "任务提交时间,")
    private Long submitTime;
    /**
     * 任务完成时间
     */
    @TableField(value = "finish_time")
    @ApiModelProperty(value = "任务完成时间")
    private Long finishTime;
    /**
     * 任务失败原因
     */
    @TableField(value = "fail_reason")
    @ApiModelProperty(value = "任务失败原因")
    private String failReason;
    /**
     * mj 任务信息
     */
    @TableField(value = "final_prompt")
    @ApiModelProperty(value = "mj 任务信息")
    private String finalPrompt;
    /**
     * 回调地址
     */
    @TableField(value = "notify_hook")
    @ApiModelProperty(value = "回调地址")
    private String notifyHook;
    /**
     * 任务关联 id
     */
    @TableField(value = "related_task_id")
    @ApiModelProperty(value = "任务关联 id")
    private Long relatedTaskId;
    /**
     * 消息 id
     */
    @TableField(value = "message_id")
    @ApiModelProperty(value = "消息 id")
    private String messageId;
    /**
     * 消息 hash
     */
    @TableField(value = "message_hash")
    @ApiModelProperty(value = "消息 hash")
    private String messageHash;
    /**
     * 任务进度
     */
    @TableField(value = "progress")
    @ApiModelProperty(value = "任务进度")
    private String progress;
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
    @TableField(value = "`action`")
    @ApiModelProperty(value = "任务类型")
    private TaskAction action;
    private String progressMessageId;
    private Integer flags;
    /**
     * 提交类型 1：web 2：公众号
     */
    private Integer subType;
    /**
     * 操作时间（每次更新时自动更新）
     */
    @TableField(value = "operate_time")
    @ApiModelProperty(value = "操作时间（每次更新时自动更新）")
    private LocalDateTime operateTime;

    @TableField(value = "topical_id")
    @ApiModelProperty(value = "主题id，关联t_use_ai_draw_topic表")
    private Long topicalId;

    @TableField(exist = false)
    @ApiModelProperty(value = "图片列表")
    List<String> imgList = new ArrayList<>();

    @JsonIgnore
    public void sleep() throws InterruptedException {
        synchronized (this.lock) {
            this.lock.wait();
        }
    }

    @JsonIgnore
    public void awake() {
        synchronized (this.lock) {
            this.lock.notifyAll();
        }
    }

    @JsonIgnore
    public void start() {
        this.startTime = System.currentTimeMillis();
        this.status = TaskStatus.SUBMITTED;
        this.progress = "0%";
    }

    @JsonIgnore
    public void success() {
        this.finishTime = System.currentTimeMillis();
        this.status = TaskStatus.SUCCESS;
        this.progress = "100%";
        try {
            ObjectId objectId = MongoDBUtil.single().storePersonFile(FileUtil.getFilePath(this.imageUrl), this.userId, "unknow");
            this.imageUrl = objectId.toString();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void fail(String reason) {
        this.finishTime = System.currentTimeMillis();
        this.status = TaskStatus.FAILURE;
        this.failReason = reason;
        this.progress = "";
    }


    public Object getLock() {
        return this.lock;
    }
}