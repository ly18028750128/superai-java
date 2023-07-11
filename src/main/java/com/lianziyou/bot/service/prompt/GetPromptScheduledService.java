package com.lianziyou.bot.service.prompt;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author Administrator
 */
@Slf4j
public class GetPromptScheduledService {

    private List<IGetPrompt> getPromptServiceList;


    public GetPromptScheduledService(List<IGetPrompt> getPromptServiceList) {
        this.getPromptServiceList = getPromptServiceList;
    }

    @Scheduled(cron = "30 10 1 * * ?")
    public void execute() {
        this.getPromptServiceList.forEach(item -> {
            try {
                item.get();
            } catch (Exception e) {
                log.error("获取提示词错误,error:{}", e.getMessage());
            }
        });
    }

}
