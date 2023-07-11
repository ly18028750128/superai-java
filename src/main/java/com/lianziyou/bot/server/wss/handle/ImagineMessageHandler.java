package com.lianziyou.bot.server.wss.handle;

import cn.hutool.core.net.URLDecoder;
import com.lianziyou.bot.controller.midjourney.support.DiscordHelper;
import com.lianziyou.bot.controller.midjourney.support.TaskCondition;
import com.lianziyou.bot.enums.mj.MessageType;
import com.lianziyou.bot.enums.mj.TaskAction;
import com.lianziyou.bot.enums.mj.TaskStatus;
import com.lianziyou.bot.model.MjTask;
import com.lianziyou.bot.model.mj.data.ContentParseData;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.springframework.stereotype.Component;

/**
 * imagine消息处理. 开始(create): **[4619231091196848] cat** - <@1012983546824114217> (Waiting to start) 进度(update): **[4619231091196848] cat** -
 * <@1012983546824114217> (0%) (relaxed) 完成(create): **[4619231091196848] cat** - <@1012983546824114217> (relaxed)
 */
@Slf4j
@Component
public class ImagineMessageHandler extends MessageHandler {

    private static final String CONTENT_REGEX = "\\*\\*(.*?)\\*\\* - <@\\d+> \\((.*?)\\)";

    @Resource
    protected DiscordHelper discordHelper;

    @Override
    public void handle(MessageType messageType, DataObject message) throws IOException {
        String content = getMessageContent(message);
        ContentParseData parseData = parse(content);
        if (parseData == null) {
            return;
        }
        String realPrompt = this.discordHelper.getRealPrompt(parseData.getPrompt());
        if (MessageType.CREATE == messageType) {
            if ("Waiting to start".equals(parseData.getStatus())) {
                // 开始
                TaskCondition condition = new TaskCondition().setActionSet(Collections.singletonList(TaskAction.IMAGINE))
                    .setStatusSet(Collections.singletonList(TaskStatus.SUBMITTED));
                MjTask mjTask = this.taskQueueHelper.findRunningTask(taskPredicate(condition, realPrompt)).findFirst().orElse(null);
                if (mjTask == null) {
                    return;
                }
                mjTask.setProgressMessageId(message.getString("id"));
                mjTask.setFinalPrompt(parseData.getPrompt());
                mjTask.setStatus(TaskStatus.IN_PROGRESS);
                mjTask.awake();
            } else {
                // 完成
                TaskCondition condition = new TaskCondition().setActionSet(Collections.singletonList(TaskAction.IMAGINE))
                    .setStatusSet(Arrays.asList(TaskStatus.SUBMITTED, TaskStatus.IN_PROGRESS));
                MjTask mjTask = this.taskQueueHelper.findRunningTask(taskPredicate(condition, realPrompt)).findFirst().orElse(null);
                if (mjTask == null) {
                    return;
                }
                mjTask.setFinalPrompt(parseData.getPrompt());
                finishTask(mjTask, message);
                mjTask.awake();
            }
        } else if (MessageType.UPDATE == messageType) {
            // 进度
            TaskCondition condition = new TaskCondition().setActionSet(Collections.singletonList(TaskAction.IMAGINE))
                .setStatusSet(Arrays.asList(TaskStatus.SUBMITTED, TaskStatus.IN_PROGRESS));
            MjTask mjTask = this.taskQueueHelper.findRunningTask(taskPredicate(condition, realPrompt)).findFirst().orElse(null);
            if (mjTask == null) {
                return;
            }
            mjTask.setProgressMessageId(message.getString("id"));
            mjTask.setFinalPrompt(parseData.getPrompt());
            mjTask.setStatus(TaskStatus.IN_PROGRESS);
            mjTask.setProgress(parseData.getStatus());
            mjTask.setImageUrl(getImageUrl(mjTask, message));
            mjTask.awake();
        }
    }

    @Override
    public void handle(MessageType messageType, Message message) throws IOException {
        String content = message.getContentRaw();
        ContentParseData parseData = parse(content);
        if (parseData == null) {
            return;
        }
        String realPrompt = this.discordHelper.getRealPrompt(parseData.getPrompt());
        if (MessageType.CREATE == messageType) {
            if ("Waiting to start".equals(parseData.getStatus())) {
                // 开始
                TaskCondition condition = new TaskCondition().setActionSet(Collections.singletonList(TaskAction.IMAGINE))
                    .setStatusSet(Collections.singletonList(TaskStatus.SUBMITTED));
                MjTask mjTask = this.taskQueueHelper.findRunningTask(taskPredicate(condition, realPrompt)).findFirst().orElse(null);
                if (mjTask == null) {
                    return;
                }
                mjTask.setProgressMessageId(message.getId());
                mjTask.setFinalPrompt(parseData.getPrompt());
                mjTask.setStatus(TaskStatus.IN_PROGRESS);
                mjTask.awake();
            } else {
                // 完成
                TaskCondition condition = new TaskCondition().setActionSet(Collections.singletonList(TaskAction.IMAGINE))
                    .setStatusSet(Arrays.asList(TaskStatus.SUBMITTED, TaskStatus.IN_PROGRESS));
                MjTask mjTask = this.taskQueueHelper.findRunningTask(taskPredicate(condition, realPrompt)).findFirst().orElse(null);
                if (mjTask == null) {
                    return;
                }
                mjTask.setFinalPrompt(parseData.getPrompt());
                finishTask(mjTask, message);
                mjTask.awake();
            }
        } else if (MessageType.UPDATE == messageType) {
            // 进度
            TaskCondition condition = new TaskCondition().setActionSet(Collections.singletonList(TaskAction.IMAGINE))
                .setStatusSet(Arrays.asList(TaskStatus.SUBMITTED, TaskStatus.IN_PROGRESS));
            MjTask mjTask = this.taskQueueHelper.findRunningTask(taskPredicate(condition, realPrompt)).findFirst().orElse(null);
            if (mjTask == null) {
                return;
            }
            mjTask.setProgressMessageId(message.getId());
            mjTask.setFinalPrompt(parseData.getPrompt());
            mjTask.setStatus(TaskStatus.IN_PROGRESS);
            mjTask.setProgress(parseData.getStatus());
            mjTask.setImageUrl(getImageUrl(mjTask, message));
            mjTask.awake();
        }
    }

    private Predicate<MjTask> taskPredicate(TaskCondition condition, String prompt) {
        return condition.and(t -> prompt.startsWith(URLDecoder.decodeForPath(t.getPromptEn(), StandardCharsets.UTF_8)));
    }

    private ContentParseData parse(String content) {
        Matcher matcher = Pattern.compile(CONTENT_REGEX).matcher(content);
        if (!matcher.find()) {
            return null;
        }
        ContentParseData parseData = new ContentParseData();
        parseData.setPrompt(matcher.group(1));
        parseData.setStatus(matcher.group(2));
        return parseData;
    }

}
