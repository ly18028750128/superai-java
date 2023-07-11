package com.lianziyou.bot.server.wss.handle;

import cn.hutool.core.text.CharSequenceUtil;
import com.lianziyou.bot.controller.midjourney.support.TaskQueueHelper;
import com.lianziyou.bot.enums.mj.MessageType;
import com.lianziyou.bot.enums.mj.TaskAction;
import com.lianziyou.bot.model.MjTask;
import com.lianziyou.bot.utils.sys.FileUtil;
import java.io.IOException;
import javax.annotation.Resource;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;

public abstract class MessageHandler {

    @Resource
    protected TaskQueueHelper taskQueueHelper;


    public abstract void handle(MessageType messageType, DataObject message) throws IOException;

    public abstract void handle(MessageType messageType, Message message) throws IOException;

    protected String getMessageContent(DataObject message) {
        return message.hasKey("content") ? message.getString("content") : "";
    }


    protected void finishTask(MjTask mjTask, DataObject message) throws IOException {
        mjTask.setMessageId(message.getString("id"));
        mjTask.setFlags(message.getInt("flags", 0));
        DataArray attachments = message.getArray("attachments");
        if (!attachments.isEmpty()) {
            String imageUrl = attachments.getObject(0).getString("url");
            mjTask.setImageUrl(imageUrl);
            mjTask.setImageUrl(replaceCdnUrl(mjTask));
            mjTask.setMessageHash(getMessageHash(imageUrl));
            mjTask.success();
        } else {
            mjTask.fail("关联图片不存在");
        }
    }

    protected void finishTask(MjTask mjTask, Message message) throws IOException {
        mjTask.setMessageId(message.getId());
        mjTask.setFlags((int) message.getFlagsRaw());
        if (!message.getAttachments().isEmpty()) {
            String imageUrl = message.getAttachments().get(0).getUrl();
            mjTask.setImageUrl(imageUrl);
            mjTask.setImageUrl(replaceCdnUrl(mjTask));
            mjTask.setMessageHash(getMessageHash(imageUrl));
            mjTask.success();
        } else {
            mjTask.fail("关联图片不存在");
        }
    }

    protected String getMessageHash(String imageUrl) {
        int hashStartIndex = imageUrl.lastIndexOf("_");
        return CharSequenceUtil.subBefore(imageUrl.substring(hashStartIndex + 1), ".", true);
    }

    protected String getImageUrl(MjTask mjTask, Message message) throws IOException {
        if (!message.getAttachments().isEmpty()) {
            String imageUrl = message.getAttachments().get(0).getUrl();
            mjTask.setImageUrl(imageUrl);
            return replaceCdnUrl(mjTask);
        }
        return null;
    }

    protected String getImageUrl(MjTask mjTask, DataObject message) throws IOException {
        DataArray attachments = message.getArray("attachments");
        if (!attachments.isEmpty()) {
            String imageUrl = attachments.getObject(0).getString("url");
            mjTask.setImageUrl(imageUrl);
            return replaceCdnUrl(mjTask);
        }
        return null;
    }

    protected String replaceCdnUrl(MjTask mjTask) throws IOException {
        if (CharSequenceUtil.isBlank(mjTask.getImageUrl())) {
            return "";
        }
        return FileUtil.base64ToImage(FileUtil.imageUrlToBase64(mjTask.getImageUrl()),
            mjTask.getAction() == TaskAction.IMAGINE ? String.valueOf(mjTask.getId()) : null);
    }

}
