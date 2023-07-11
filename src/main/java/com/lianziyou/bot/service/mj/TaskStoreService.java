package com.lianziyou.bot.service.mj;


import com.lianziyou.bot.controller.midjourney.support.TaskCondition;
import com.lianziyou.bot.model.MjTask;

public interface TaskStoreService {

    void save(MjTask mjTask);

    void delete(Long id);

    MjTask get(Long id);


    MjTask findNotFailOne(TaskCondition taskCondition);


}
