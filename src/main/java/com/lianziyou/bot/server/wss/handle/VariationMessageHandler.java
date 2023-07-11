package com.lianziyou.bot.server.wss.handle;

import cn.hutool.core.text.CharSequenceUtil;
import com.lianziyou.bot.controller.midjourney.support.TaskCondition;
import com.lianziyou.bot.enums.mj.MessageType;
import com.lianziyou.bot.enums.mj.TaskAction;
import com.lianziyou.bot.enums.mj.TaskStatus;
import com.lianziyou.bot.model.MjTask;
import com.lianziyou.bot.model.mj.data.UVContentParseData;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.springframework.stereotype.Component;

/**
 * variation消息处理. 开始(create): Making variations for image #1 with prompt **cat** - <@1012983546824114217> (Waiting to start) 进度(update): **cat** - Variations
 * (Strong) by <@1012983546824114217> (0%) (relaxed) 5.2前-进度(update): **cat** - Variations by <@1012983546824114217> (0%) (relaxed) 完成(create): **cat** -
 * Variations (Strong) by <@1012983546824114217> (relaxed) 5.2前-完成(create): **cat** - Variations by <@1012983546824114217> (relaxed)
 */
@Slf4j
@Component
public class VariationMessageHandler extends MessageHandler {

    private static final String START_CONTENT_REGEX = "Making variations for image #(\\d) with prompt \\*\\*(.*?)\\*\\* - <@\\d+> \\((.*?)\\)";
    private static final String OLD_CONTENT_REGEX = "\\*\\*(.*?)\\*\\* - Variations by <@\\d+> \\((.*?)\\)";
    private static final String CONTENT_REGEX = "\\*\\*(.*?)\\*\\* - Variations \\(Strong\\) by <@\\d+> \\((.*?)\\)";

    @Override
    public void handle(MessageType messageType, DataObject message) throws IOException {
        String content = getMessageContent(message);
        if (MessageType.CREATE.equals(messageType)) {
            UVContentParseData start = parseStart(content);
            if (start != null) {
                // 开始
                TaskCondition condition = new TaskCondition()
                    .setFinalPromptEn(start.getPrompt())
                    .setActionSet(Collections.singletonList(TaskAction.VARIATION))
                    .setStatusSet(Collections.singletonList(TaskStatus.SUBMITTED));
                MjTask mjTask = this.taskQueueHelper.findRunningTask(condition)
                    .filter(t -> CharSequenceUtil.endWith(t.getDescription(), "V" + start.getIndex()))
                    .min(Comparator.comparing(MjTask::getSubmitTime))
                    .orElse(null);
                if (mjTask == null) {
                    return;
                }
                mjTask.setProgressMessageId(message.getString("id"));
                mjTask.setStatus(TaskStatus.IN_PROGRESS);
                mjTask.awake();
                return;
            }
            UVContentParseData end = parse(content);
            if (end == null) {
                return;
            }
            TaskCondition condition = new TaskCondition()
                .setFinalPromptEn(end.getPrompt())
                .setActionSet(Collections.singletonList(TaskAction.VARIATION))
                .setStatusSet(Arrays.asList(TaskStatus.SUBMITTED, TaskStatus.IN_PROGRESS));
            MjTask mjTask = this.taskQueueHelper.findRunningTask(condition)
                .max(Comparator.comparing(MjTask::getProgress))
                .orElse(null);
            if (mjTask == null) {
                return;
            }
            finishTask(mjTask, message);
            mjTask.awake();
        } else if (MessageType.UPDATE == messageType) {
            UVContentParseData parseData = parse(content);
            if (parseData == null || CharSequenceUtil.equalsAny(parseData.getStatus(), "relaxed", "fast")) {
                return;
            }
            TaskCondition condition = new TaskCondition()
                .setProgressMessageId(message.getString("id"))
                .setActionSet(Collections.singletonList(TaskAction.VARIATION))
                .setStatusSet(Arrays.asList(TaskStatus.SUBMITTED, TaskStatus.IN_PROGRESS));
            MjTask mjTask = this.taskQueueHelper.findRunningTask(condition)
                .findFirst().orElse(null);
            if (mjTask == null) {
                return;
            }
            mjTask.setProgressMessageId(message.getString("id"));
            mjTask.setStatus(TaskStatus.IN_PROGRESS);
            mjTask.setProgress(parseData.getStatus());
            getImageUrl(mjTask, message);
            mjTask.awake();
        }
    }

    /**
     * bot-wss模式，取不到执行进度; todo: 同个任务不同变换对应不上.
     *
     * @param messageType messageType
     * @param message     message
     */
    @Override
    public void handle(MessageType messageType, Message message) throws IOException {
        String content = message.getContentRaw();
        if (MessageType.CREATE.equals(messageType)) {
            UVContentParseData parseData = parse(content);
            if (parseData == null) {
                return;
            }
            TaskCondition condition = new TaskCondition()
                .setFinalPromptEn(parseData.getPrompt())
                .setActionSet(Collections.singletonList(TaskAction.VARIATION))
                .setStatusSet(Arrays.asList(TaskStatus.SUBMITTED, TaskStatus.IN_PROGRESS));
            MjTask mjTask = this.taskQueueHelper.findRunningTask(condition)
                .min(Comparator.comparing(MjTask::getSubmitTime))
                .orElse(null);
            if (mjTask == null) {
                return;
            }
            finishTask(mjTask, message);
            mjTask.awake();
        }
    }

    private UVContentParseData parseStart(String content) {
        Matcher matcher = Pattern.compile(START_CONTENT_REGEX).matcher(content);
        if (!matcher.find()) {
            return null;
        }
        UVContentParseData parseData = new UVContentParseData();
        parseData.setIndex(Integer.parseInt(matcher.group(1)));
        parseData.setPrompt(matcher.group(2));
        parseData.setStatus(matcher.group(3));
        return parseData;
    }

    private UVContentParseData parse(String content) {
        UVContentParseData data = parse(content, CONTENT_REGEX);
        if (data == null) {
            return parse(content, OLD_CONTENT_REGEX);
        }
        return data;
    }

    private UVContentParseData parse(String content, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(content);
        if (!matcher.find()) {
            return null;
        }
        UVContentParseData parseData = new UVContentParseData();
        parseData.setPrompt(matcher.group(1));
        parseData.setStatus(matcher.group(2));
        return parseData;
    }

}
