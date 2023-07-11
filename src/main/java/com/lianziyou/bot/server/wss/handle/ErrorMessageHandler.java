package com.lianziyou.bot.server.wss.handle;

import cn.hutool.core.text.CharSequenceUtil;
import com.lianziyou.bot.controller.midjourney.support.DiscordHelper;
import com.lianziyou.bot.controller.midjourney.support.TaskCondition;
import com.lianziyou.bot.enums.mj.MessageType;
import com.lianziyou.bot.enums.mj.TaskAction;
import com.lianziyou.bot.model.MjTask;
import com.lianziyou.bot.model.User;
import com.lianziyou.bot.service.sys.AsyncService;
import com.lianziyou.bot.service.sys.IUserService;
import java.io.IOException;
import java.util.Optional;
import javax.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ErrorMessageHandler extends MessageHandler {

    @Resource
    protected DiscordHelper discordHelper;
    @Resource
    AsyncService asyncService;
    @Resource
    IUserService userService;
    @Resource
    WxMpService wxMpService;

    @SneakyThrows
    @Override
    public void handle(MessageType messageType, DataObject message) throws IOException {
        Optional<DataArray> embedsOptional = message.optArray("embeds");
        if (!embedsOptional.isPresent() || embedsOptional.get().isEmpty()) {
            return;
        }
        DataObject embed = embedsOptional.get().getObject(0);
        String title = embed.getString("title", null);
        if (CharSequenceUtil.isBlank(title) || CharSequenceUtil.startWith(title, "Your info - ")) {
            // 排除正常信息.
            return;
        }
        String description = embed.getString("description", null);
        String footerText = "";
        Optional<DataObject> footer = embed.optObject("footer");
        if (footer.isPresent()) {
            footerText = footer.get().getString("text", "");
        }
        log.warn("检测到可能异常的信息: {}\n{}\nfooter: {}", title, description, footerText);
        if (CharSequenceUtil.contains(description, "this job will start")) {
            // mj队列中, 不认为是异常
            return;
        }
        if (CharSequenceUtil.contains(description, "verify you're human")) {
            String reason = "需要人工验证，请联系管理员";
            this.taskQueueHelper.findRunningTask(new TaskCondition()).forEach(task -> {
                task.fail(reason);
                task.awake();
            });
            return;
        }
        MjTask targetMjMjTask = null;
        if (CharSequenceUtil.startWith(footerText, "/imagine ")) {
            String finalPrompt = CharSequenceUtil.subAfter(footerText, "/imagine ", false);
            if (CharSequenceUtil.contains(finalPrompt, "https://")) {
                // 有可能为blend操作
                String taskId = this.discordHelper.findTaskIdWithCdnUrl(finalPrompt.split(" ")[0]);
                if (taskId != null) {
                    targetMjMjTask = this.taskQueueHelper.getRunningTask(Long.valueOf(taskId));
                }
            }
            if (targetMjMjTask == null) {
                targetMjMjTask = this.taskQueueHelper.findRunningTask(t ->
                        t.getAction() == TaskAction.IMAGINE && finalPrompt.startsWith(t.getPromptEn()))
                    .findFirst().orElse(null);
            }
        } else if (CharSequenceUtil.startWith(footerText, "/describe ")) {
            String imageUrl = CharSequenceUtil.subAfter(footerText, "/describe ", false);
            String taskId = this.discordHelper.findTaskIdWithCdnUrl(imageUrl);
            targetMjMjTask = this.taskQueueHelper.getRunningTask(Long.valueOf(taskId));
        }
        if (targetMjMjTask == null) {
            return;
        }
        String reason;
        if (CharSequenceUtil.contains(description, "against our community standards")) {
            reason = "可能包含违规信息";
        } else {
            reason = description;
        }
        if (targetMjMjTask.getSubType() == 2) {
            User user = userService.getById(targetMjMjTask.getUserId());
            WxMpKefuMessage wxMpKefuMessage = WxMpKefuMessage.TEXT().toUser(user.getFromUserName()).content(reason).build();
            wxMpService.getKefuService().sendKefuMessage(wxMpKefuMessage);
        }
        targetMjMjTask.fail(reason);
        targetMjMjTask.awake();
        asyncService.updateMjTask(targetMjMjTask);
    }

    @Override
    public void handle(MessageType messageType, Message message) {
        // bot-wss 获取不到错误
    }

}
