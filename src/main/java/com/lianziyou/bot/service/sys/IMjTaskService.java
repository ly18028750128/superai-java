package com.lianziyou.bot.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lianziyou.bot.model.MjTask;


public interface IMjTaskService extends IService<MjTask> {


    /**
     * 清空 mj 任务
     *
     * @param userId
     * @return
     */
    int emptyMjTask(Long userId);

    /**
     * 删除 mj 任务
     *
     * @param id
     * @return
     */
    int deleteMjTask(Long id);

}
