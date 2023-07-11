package com.lianziyou.bot.listener;

import com.lianziyou.bot.constant.SseConst;
import com.lianziyou.bot.model.sse.GptMessageVo;
import com.lianziyou.bot.model.sse.MidMessageVo;
import com.lianziyou.bot.model.sse.SdMessageVo;
import com.lianziyou.bot.server.SseEmitterServer;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

@Service
public class TopicListener implements ApplicationRunner, Ordered {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicListener.class);

    @Autowired
    private RedissonClient redisson;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        redisson.getTopic(SseConst.getSseGptTopic()).addListener(GptMessageVo.class, (CharSequence charSequence, GptMessageVo message) -> {
            SseEmitterServer.sendMessage(message.getUserId(), message.getMessage());
        });

        redisson.getTopic(SseConst.getSseSdTopic()).addListener(SdMessageVo.class, (CharSequence charSequence, SdMessageVo message) -> {
            SseEmitterServer.sendMessage(message.getUserId(), message.getMessage());
        });

        redisson.getTopic(SseConst.getSseMidTopic()).addListener(MidMessageVo.class, (CharSequence charSequence, MidMessageVo message) -> {
            SseEmitterServer.sendMessage(message.getUserId(), message.getMessage());
        });

    }

    @Override
    public int getOrder() {
        return 1;
    }
}