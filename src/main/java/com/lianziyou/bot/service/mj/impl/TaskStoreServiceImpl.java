package com.lianziyou.bot.service.mj.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lianziyou.bot.base.exception.BussinessException;
import com.lianziyou.bot.constant.AiDrawConst.TopicalType;
import com.lianziyou.bot.controller.midjourney.support.TaskCondition;
import com.lianziyou.bot.enums.mj.TaskStatus;
import com.lianziyou.bot.model.MjTask;
import com.lianziyou.bot.model.UserAiDrawTopical;
import com.lianziyou.bot.service.draw.UserAiDrawTopicalService;
import com.lianziyou.bot.service.mj.TaskStoreService;
import com.lianziyou.bot.service.sys.IMjTaskService;
import com.lianziyou.bot.utils.MyBatisPlusUtil;
import java.time.LocalDateTime;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = BussinessException.class)
public class TaskStoreServiceImpl implements TaskStoreService {

    @Resource
    IMjTaskService mjTaskService;

    @Resource
    UserAiDrawTopicalService aiDrawTopicalService;

    @Override
    public void save(MjTask mjTask) {

        if (ObjectUtil.isEmpty(mjTask.getTopicalId())) {
            UserAiDrawTopical userAiDrawTopical = UserAiDrawTopical.builder().userId(mjTask.getUserId()).name(mjTask.getPrompt()).type(TopicalType.MID.value)
                .creator(mjTask.getUserId()).createTime(LocalDateTime.now()).build();
            aiDrawTopicalService.save(userAiDrawTopical);
            mjTask.setTopicalId(userAiDrawTopical.getId());
        }
        mjTaskService.saveOrUpdate(mjTask);
    }

    @Override
    public void delete(Long id) {
        mjTaskService.removeById(id);
    }

    @Override
    public MjTask get(Long id) {
        return mjTaskService.getById(id);
    }

    @Override
    public MjTask findNotFailOne(TaskCondition taskCondition) {
        QueryWrapper<MjTask> queryWrapper = MyBatisPlusUtil.single().getPredicate(taskCondition);
        queryWrapper.ne("status", TaskStatus.FAILURE.name());
        return mjTaskService.getOne(queryWrapper);
    }


}
