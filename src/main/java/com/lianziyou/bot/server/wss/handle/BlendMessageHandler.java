package com.lianziyou.bot.server.wss.handle;


import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import com.lianziyou.bot.controller.midjourney.support.DiscordHelper;
import com.lianziyou.bot.controller.midjourney.support.TaskCondition;
import com.lianziyou.bot.controller.midjourney.support.TaskQueueHelper;
import com.lianziyou.bot.enums.mj.MessageType;
import com.lianziyou.bot.enums.mj.TaskAction;
import com.lianziyou.bot.enums.mj.TaskStatus;
import com.lianziyou.bot.model.MjTask;
import com.lianziyou.bot.model.mj.data.ContentParseData;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.springframework.stereotype.Component;

/**
 * blend消息处理. 开始(create): **<https://s.mj.run/JWu6jaL1D-8> <https://s.mj.run/QhfnQY-l68o> --v 5.1** - <@1012983546824114217> (Waiting to start) 进度(update):
 * **<https://s.mj.run/JWu6jaL1D-8> <https://s.mj.run/QhfnQY-l68o> --v 5.1** - <@1012983546824114217> (0%) (relaxed) 完成(create):
 * **<https://s.mj.run/JWu6jaL1D-8> <https://s.mj.run/QhfnQY-l68o> --v 5.1** - <@1012983546824114217> (relaxed)
 */
@Component
public class BlendMessageHandler extends MessageHandler {

    private static final String CONTENT_REGEX = "\\*\\*(.*?)\\*\\* - <@\\d+> \\((.*?)\\)";
    @Resource
    protected DiscordHelper discordHelper;
    @Resource
    protected TaskQueueHelper taskQueueHelper;

    @Override
    public void handle(MessageType messageType, DataObject message) throws IOException {
        Optional<DataObject> interaction = message.optObject("interaction");
        String content = getMessageContent(message);
        String interactionName = interaction.map(dataObject -> dataObject.getString("name")).orElse("");
        boolean match = (CharSequenceUtil.startWith(content, "**<" + DiscordHelper.SIMPLE_URL_PREFIX)) && ObjectUtil.equal("blend", interactionName);
        if (!match) {
            return;
        }
        ContentParseData parseData = parse(content);
        if (parseData == null) {
            return;
        }
        if (MessageType.CREATE == messageType) {
            if ("Waiting to start".equals(parseData.getStatus())) {
                // 开始
                List<String> urls = CharSequenceUtil.split(parseData.getPrompt(), " ");
                if (urls.isEmpty()) {
                    return;
                }
                String url = getRealUrl(urls.get(0));
                TaskCondition condition = new TaskCondition().setContainUrl(url).setActionSet(Collections.singletonList(TaskAction.BLEND))
                    .setStatusSet(Collections.singletonList(TaskStatus.SUBMITTED));
                MjTask mjTask = this.taskQueueHelper.findRunningTask(condition).findFirst().orElse(null);
                if (mjTask == null) {
                    return;
                }
                mjTask.setProgressMessageId(message.getString("id"));
                mjTask.setPrompt(parseData.getPrompt());
                mjTask.setPromptEn(parseData.getPrompt());
                mjTask.setStatus(TaskStatus.IN_PROGRESS);
                mjTask.awake();
            } else {
                // 完成
                TaskCondition condition = new TaskCondition().setActionSet(Collections.singletonList(TaskAction.BLEND))
                    .setStatusSet(Collections.singletonList(TaskStatus.IN_PROGRESS));
                MjTask mjTask = this.taskQueueHelper.findRunningTask(condition).max(Comparator.comparing(MjTask::getProgress)).orElse(null);
                if (mjTask == null) {
                    return;
                }
                mjTask.setFinalPrompt(parseData.getPrompt());
                finishTask(mjTask, message);
                mjTask.awake();
            }
        } else if (MessageType.UPDATE == messageType) {
            // 进度
            TaskCondition condition = new TaskCondition().setProgressMessageId(message.getString("id"))
                .setActionSet(Collections.singletonList(TaskAction.BLEND)).setStatusSet(Collections.singletonList(TaskStatus.IN_PROGRESS));
            MjTask mjTask = this.taskQueueHelper.findRunningTask(condition).findFirst().orElse(null);
            if (mjTask == null) {
                return;
            }
            mjTask.setProgressMessageId(message.getString("id"));
            mjTask.setProgress(parseData.getStatus());
            mjTask.setImageUrl(getImageUrl(mjTask, message));
            mjTask.awake();
        }
    }

    @Override
    public void handle(MessageType messageType, Message message) throws IOException {
        String content = message.getContentRaw();
        String interactionName = message.getInteraction().getName() == null ? null : message.getInteraction().getName();
        boolean match = (CharSequenceUtil.startWith(content, "**<" + DiscordHelper.SIMPLE_URL_PREFIX)) && ObjectUtil.equal("blend", interactionName);
        if (!match) {
            return;
        }
        ContentParseData parseData = parse(content);
        if (parseData == null) {
            return;
        }
        if (MessageType.CREATE == messageType) {
            if ("Waiting to start".equals(parseData.getStatus())) {
                // 开始
                List<String> urls = CharSequenceUtil.split(parseData.getPrompt(), " ");
                if (urls.isEmpty()) {
                    return;
                }
                String url = getRealUrl(urls.get(0));
                TaskCondition condition = new TaskCondition().setContainUrl(url).setActionSet(Collections.singletonList(TaskAction.BLEND))
                    .setStatusSet(Collections.singletonList(TaskStatus.SUBMITTED));
                MjTask mjTask = this.taskQueueHelper.findRunningTask(condition).findFirst().orElse(null);
                if (mjTask == null) {
                    return;
                }
                mjTask.setProgressMessageId(message.getId());
                mjTask.setPrompt(parseData.getPrompt());
                mjTask.setPromptEn(parseData.getPrompt());
                mjTask.setStatus(TaskStatus.IN_PROGRESS);
                mjTask.awake();
            } else {
                // 完成
                TaskCondition condition = new TaskCondition().setActionSet(Collections.singletonList(TaskAction.BLEND))
                    .setStatusSet(Collections.singletonList(TaskStatus.IN_PROGRESS));
                MjTask mjTask = this.taskQueueHelper.findRunningTask(condition).max(Comparator.comparing(MjTask::getProgress)).orElse(null);
                if (mjTask == null) {
                    return;
                }
                mjTask.setFinalPrompt(parseData.getPrompt());
                finishTask(mjTask, message);
                mjTask.awake();
            }
        } else if (MessageType.UPDATE == messageType) {
            // 进度
            TaskCondition condition = new TaskCondition().setProgressMessageId(message.getId()).setActionSet(Collections.singletonList(TaskAction.BLEND))
                .setStatusSet(Collections.singletonList(TaskStatus.IN_PROGRESS));
            MjTask mjTask = this.taskQueueHelper.findRunningTask(condition).findFirst().orElse(null);
            if (mjTask == null) {
                return;
            }
            mjTask.setProgressMessageId(message.getId());
            mjTask.setProgress(parseData.getStatus());
            mjTask.setImageUrl(getImageUrl(mjTask, message));
            mjTask.awake();
        }
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

    private String getRealUrl(String url) {
        if (CharSequenceUtil.startWith(url, "<" + DiscordHelper.SIMPLE_URL_PREFIX)) {
            return this.discordHelper.getRealUrl(url.substring(1, url.length() - 1));
        }
        return url;
    }
}
