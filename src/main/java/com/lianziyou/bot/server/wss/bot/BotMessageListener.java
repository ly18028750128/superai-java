package com.lianziyou.bot.server.wss.bot;

import com.lianziyou.bot.base.exception.BussinessException;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.enums.mj.MessageType;
import com.lianziyou.bot.model.SysConfig;
import com.lianziyou.bot.server.wss.handle.MessageHandler;
import com.lianziyou.bot.utils.sys.RedisUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

@Slf4j
public class BotMessageListener extends ListenerAdapter implements ApplicationListener<ApplicationStartedEvent> {

    private final List<MessageHandler> messageHandlers = new ArrayList<>();

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        this.messageHandlers.addAll(event.getApplicationContext().getBeansOfType(MessageHandler.class).values());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        if (ignoreAndLogMessage(message, MessageType.CREATE)) {
            return;
        }
        for (MessageHandler messageHandler : this.messageHandlers) {
            try {
                messageHandler.handle(MessageType.CREATE, message);
            } catch (IOException e) {
                throw new BussinessException(e.getMessage());
            }
        }
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        Message message = event.getMessage();
        if (ignoreAndLogMessage(message, MessageType.UPDATE)) {
            return;
        }
        for (MessageHandler messageHandler : this.messageHandlers) {
            try {
                messageHandler.handle(MessageType.UPDATE, message);
            } catch (IOException e) {
                throw new BussinessException(e.getMessage());
            }
        }
    }

    private boolean ignoreAndLogMessage(Message message, MessageType messageType) {
        String channelId = message.getChannel().getId();
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);

        if (!sysConfig.getMjChannelId().equals(channelId)) {
            return true;
        }
        String authorName = message.getAuthor().getName();
        log.debug("{} - {}: {}", messageType.name(), authorName, message.getContentRaw());
        return !sysConfig.getMjBotName().equals(authorName);
    }

}
