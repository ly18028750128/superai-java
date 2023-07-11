package com.lianziyou.bot.config;

import com.lianziyou.bot.service.prompt.GetPromptScheduledService;
import com.lianziyou.bot.service.prompt.IGetPrompt;
import java.util.stream.Collectors;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GetPromptConfig {

    @Bean
    public GetPromptScheduledService getPromptScheduled(ObjectProvider<IGetPrompt> iGetPrompts) {
        return new GetPromptScheduledService(iGetPrompts.stream().collect(Collectors.toList()));
    }


}
