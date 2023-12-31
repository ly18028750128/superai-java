package com.lianziyou.bot.server.wss.handle;

import cn.hutool.core.text.CharSequenceUtil;
import com.lianziyou.bot.enums.mj.MessageType;
import com.lianziyou.bot.model.MjTask;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.springframework.stereotype.Component;

/**
 * describe消息处理.
 */
@Component
public class DescribeMessageHandler extends MessageHandler {

    @Override
    public void handle(MessageType messageType, DataObject message) throws IOException {
        Optional<DataObject> interaction = message.optObject("interaction");
        if (!interaction.isPresent() || !"describe".equals(interaction.get().getString("name"))) {
            return;
        }
        DataArray embeds = message.getArray("embeds");
        if (embeds.isEmpty()) {
            return;
        }
        String prompt = embeds.getObject(0).getString("description");
        Optional<DataObject> imageOptional = embeds.getObject(0).optObject("image");
        if (!imageOptional.isPresent()) {
            return;
        }
        String imageUrl = imageOptional.get().getString("url");
        int hashStartIndex = imageUrl.lastIndexOf("/");
        String taskId = CharSequenceUtil.subBefore(imageUrl.substring(hashStartIndex + 1), ".", true);
        MjTask mjTask = this.taskQueueHelper.getRunningTask(Long.valueOf(taskId));
        if (mjTask == null) {
            return;
        }
        mjTask.setMessageId(message.getString("id"));
        mjTask.setFlags(message.getInt("flags", 0));
        mjTask.setPrompt(prompt);
        mjTask.setPromptEn(prompt);
        mjTask.setImageUrl(replaceCdnUrl(mjTask));
        mjTask.success();
        mjTask.awake();
    }

    @Override
    public void handle(MessageType messageType, Message message) throws IOException {
        if (message.getInteraction() == null || !"describe".equals(message.getInteraction().getName())) {
            return;
        }
        List<MessageEmbed> embeds = message.getEmbeds();
        if (embeds.isEmpty()) {
            return;
        }
        String prompt = embeds.get(0).getDescription();
        String imageUrl = embeds.get(0).getImage().getUrl();
        int hashStartIndex = imageUrl.lastIndexOf("/");
        String taskId = CharSequenceUtil.subBefore(imageUrl.substring(hashStartIndex + 1), ".", true);
        MjTask mjTask = this.taskQueueHelper.getRunningTask(Long.valueOf(taskId));
        if (mjTask == null) {
            return;
        }
        mjTask.setMessageId(message.getId());
        mjTask.setFlags((int) message.getFlagsRaw());
        mjTask.setPrompt(prompt);
        mjTask.setPromptEn(prompt);
        mjTask.setImageUrl(replaceCdnUrl(mjTask));
        mjTask.success();
        mjTask.awake();
    }

}
