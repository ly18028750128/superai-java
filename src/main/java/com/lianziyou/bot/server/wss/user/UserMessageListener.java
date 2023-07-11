package com.lianziyou.bot.server.wss.user;


import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.enums.mj.MessageType;
import com.lianziyou.bot.model.SysConfig;
import com.lianziyou.bot.server.wss.handle.MessageHandler;
import com.lianziyou.bot.utils.sys.RedisUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

@Slf4j
public class UserMessageListener {

    private final List<MessageHandler> messageHandlers;

    public UserMessageListener(ObjectProvider<List<MessageHandler>> objectProvider){
        this.messageHandlers = objectProvider.getIfAvailable();
    }

    public void onMessage(DataObject raw) throws IOException {
        MessageType messageType = MessageType.of(raw.getString("t"));
        if (messageType == null) {
            return;
        }
        DataObject data = raw.getObject("d");
        if (ignoreAndLogMessage(data, messageType)) {
            return;
        }
        for (MessageHandler messageHandler : this.messageHandlers) {
            messageHandler.handle(messageType, data);
        }
    }

    private boolean ignoreAndLogMessage(DataObject data, MessageType messageType) {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);

        String channelId = data.getString("channel_id");
        if (!sysConfig.getMjChannelId().equals(channelId)) {
            return true;
        }
        Optional<DataObject> author = data.optObject("author");
        if (!author.isPresent()) {
            return true;
        }
        String authorName = author.get().getString("username");
        log.debug("{} - {}: {}", messageType.name(), authorName, data.getString("content"));
        return !sysConfig.getMjBotName().equals(authorName);
    }
}