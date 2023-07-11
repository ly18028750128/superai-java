package com.lianziyou.bot.service.sys;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.lianziyou.bot.base.exception.BussinessException;
import com.lianziyou.bot.model.AiPrompt;
import com.lianziyou.bot.model.MessageLog;
import com.lianziyou.bot.model.User;
import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("CheckService")
@Transactional(rollbackFor = BussinessException.class)
public class CheckService {

    @Resource
    IUserService userService;

    @Resource
    IMessageLogService useLogService;

    @Resource
    AiPromptService aiPromptService;


    public Long checkAndSaveMessageLog(@NotNull final MessageLog messageLog, Long id, String problem) {
        //查询当前用户信息
        try {
            userService.lambdaUpdate().setSql(String.format("remaining_times = remaining_times - %d", messageLog.getUseNumber()))
                .eq(User::getId, messageLog.getUserId()).update();
        } catch (Exception e) {
            throw new BussinessException("剩余次数不足请充值");
        }
        if (null != id) {
            useLogService.lambdaUpdate().set(MessageLog::getUseValue, messageLog.getUseValue())
                .setSql(String.format("use_number = use_number + %d", messageLog.getUseNumber())).eq(MessageLog::getId, id).update();
            messageLog.setId(id);
        } else {

            if (ObjectUtil.isNotNull(messageLog.getInitPromptId())) {
                AiPrompt aiPrompt = aiPromptService.lambdaQuery().select(AiPrompt::getInitPrompt,AiPrompt::getTagCn).eq(AiPrompt::getIsPublish, 1)
                    .eq(AiPrompt::getId, messageLog.getInitPromptId()).one();
                if (aiPrompt != null) {
                    messageLog.setInitPrompt(aiPrompt.getInitPrompt());
                    messageLog.setTagName(aiPrompt.getTagCn());
                }
            }
            if (problem.length() > 100) {
                messageLog.setName(problem.substring(0, 100));
            } else {
                messageLog.setName(problem);
            }

            messageLog.setUseType(1);  // 次数卡
            useLogService.save(messageLog);
        }

        BeanUtil.copyProperties(useLogService.getById(id), messageLog);

        return messageLog.getId();
    }

    public void checkAndSaveMessageLog(Long userId, Integer number) {
        //查询当前用户信息
        User user = userService.getById(userId);
        if (user.getType() != -1) {
            //判断剩余次数
            if (user.getRemainingTimes() < number) {
                throw new BussinessException("剩余次数不足请充值");
            }
            user.setRemainingTimes(user.getRemainingTimes() - number);
        }
        userService.saveOrUpdate(user);
    }
}
