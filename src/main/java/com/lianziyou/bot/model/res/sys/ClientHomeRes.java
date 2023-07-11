package com.lianziyou.bot.model.res.sys;

import com.lianziyou.bot.model.UserAiDrawTopical;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientHomeRes {

    /**
     * 对话记录
     */
    List<ClientHomeLogRes> logList;
    /**
     * mj任务列表
     */
    List<MjTaskRes> mjTaskList;

    /**
     * Ai绘画主题列表
     */
    List<UserAiDrawTopical> aiDrawTopicalList;

    /**
     * 用户id
     */
    private Long id;
    /**
     * 姓名
     */
    private String name;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 上次登录时间
     */
    private LocalDateTime lastLoginTime;
    /**
     * 剩余次数
     */
    private Integer remainingTimes;
    /**
     * 置顶公告
     */
    private String announcement;

}
