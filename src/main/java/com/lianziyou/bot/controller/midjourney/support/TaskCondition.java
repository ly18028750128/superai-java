package com.lianziyou.bot.controller.midjourney.support;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.text.CharSequenceUtil;
import com.lianziyou.bot.annotate.Query;
import com.lianziyou.bot.annotate.Query.Type;
import com.lianziyou.bot.enums.mj.TaskAction;
import com.lianziyou.bot.enums.mj.TaskStatus;
import com.lianziyou.bot.model.MjTask;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class TaskCondition implements Predicate<MjTask> {

    @Query
    private Long id;

    @Query(propName = "status", type = Type.IN)
    private List<TaskStatus> statusSet = new ArrayList<>();

    @Query(propName = "action", type = Type.IN)
    private List<TaskAction> actionSet;

    @Query
    private String prompt;
    @Query
    private String promptEn;
    @Query
    private String description;
    @Query
    private String finalPromptEn;
    @Query
    private Long relatedTaskId;
    @Query
    private String messageId;
    @Query
    private String progressMessageId;

    @Query(ignore = true)
    private String containUrl;  // 按图片地址查询


    @Override
    public boolean test(MjTask mjTask) {
        if (null != this.id && !Objects.equals(this.id, mjTask.getId())) {
            return false;
        }
        if (this.statusSet != null && !this.statusSet.isEmpty() && !this.statusSet.contains(mjTask.getStatus())) {
            return false;
        }
        if (this.actionSet != null && !this.actionSet.isEmpty() && !this.actionSet.contains(mjTask.getAction())) {
            return false;
        }
        if (CharSequenceUtil.isNotBlank(this.prompt) && !this.prompt.equals(mjTask.getPrompt())) {
            return false;
        }
        if (CharSequenceUtil.isNotBlank(this.promptEn) && !this.promptEn.equals(mjTask.getPromptEn())) {
            return false;
        }
        if (CharSequenceUtil.isNotBlank(this.description) && !this.description.equals(mjTask.getDescription())) {
            return false;
        }

        if (CharSequenceUtil.isNotBlank(this.finalPromptEn) && !this.finalPromptEn.equals(mjTask.getFinalPrompt())) {
            return false;
        }
        if (null != this.relatedTaskId && !this.relatedTaskId.equals(mjTask.getRelatedTaskId())) {
            return false;
        }
        if (CharSequenceUtil.isNotBlank(this.messageId) && !this.messageId.equals(mjTask.getMessageId())) {
            return false;
        }
        if (CharSequenceUtil.isNotBlank(this.progressMessageId) && !this.progressMessageId.equals(mjTask.getProgressMessageId())) {
            return false;
        }
        if (CharSequenceUtil.isNotBlank(this.containUrl)) {
            if (CollectionUtil.isNotEmpty(mjTask.getImgList())) {
                return false;
            }
            for (int idx = 0; idx < mjTask.getImgList().size(); idx++) {
                String url = URLDecoder.decodeForPath(mjTask.getImgList().get(idx), Charset.defaultCharset());
                if (!URLDecoder.decodeForPath(containUrl, Charset.defaultCharset()).equals(url)) {
                    return false;
                }
            }
        }

        return true;
    }


}
