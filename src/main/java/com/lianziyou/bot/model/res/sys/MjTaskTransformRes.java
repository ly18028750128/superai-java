package com.lianziyou.bot.model.res.sys;

import com.lianziyou.bot.enums.mj.TaskAction;
import lombok.Data;

@Data
public class MjTaskTransformRes {

    /**
     * 任务类型
     */
    private TaskAction action;


    /**
     * 图片位置
     */
    private Integer index;

    /**
     * 关联任务id
     */
    private Long relatedTaskId;

}
