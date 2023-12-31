package com.lianziyou.bot.model.req.mj;

import com.lianziyou.bot.enums.mj.TaskAction;
import com.lianziyou.bot.enums.mj.TaskStatus;
import java.io.Serializable;
import lombok.Data;


@Data
public class MjCallBack implements Serializable {

    /**
     * 图片url
     */
    private String imageUrl;


    /**
     * 用户id
     */
    private Long userId;

    /**
     * id
     */
    private Long id;

    /**
     * 任务状态
     */
    private TaskStatus status;

    /**
     * 任务进度
     */
    private String progress;


    /**
     * 提交类型 1：web 2：公众号
     */
    private Integer subType;

    /**
     * 任务类型
     */
    private TaskAction action;
}
