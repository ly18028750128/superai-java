package com.lianziyou.bot.service.mj.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.lianziyou.bot.base.exception.BussinessException;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.controller.midjourney.support.TaskQueueHelper;
import com.lianziyou.bot.enums.mj.BlendDimensions;
import com.lianziyou.bot.enums.sys.ResultEnum;
import com.lianziyou.bot.model.MjTask;
import com.lianziyou.bot.service.mj.DiscordService;
import com.lianziyou.bot.service.mj.TaskService;
import com.lianziyou.bot.utils.mj.MimeTypeUtils;
import eu.maxschuster.dataurl.DataUrl;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    @Resource
    TaskQueueHelper taskQueueHelper;
    @Resource
    private DiscordService discordService;

    @Override
    public ApiResult<Void> submitImagine(final MjTask mjTask) {
        return this.taskQueueHelper.submitTask(mjTask, () -> {
            if (CollectionUtil.isNotEmpty(mjTask.getImgList())) {
                StringBuilder image = new StringBuilder();
                for (String img : mjTask.getImgList()) {
                    image.append(img).append(" ");
                }
                mjTask.setPromptEn(image + mjTask.getPromptEn());
                mjTask.setPrompt(image + mjTask.getPrompt());
            }
            mjTask.setDescription("/imagine " + mjTask.getPrompt());
            return this.discordService.imagine(mjTask.getPromptEn());
        });
    }

    @Override
    public ApiResult<Void> submitUpscale(MjTask mjTask, String targetMessageId, String targetMessageHash, int index, int messageFlags) {
        return this.taskQueueHelper.submitTask(mjTask, () -> this.discordService.upscale(targetMessageId, index, targetMessageHash, messageFlags));
    }

    @Override
    public ApiResult<Void> submitVariation(MjTask mjTask, String targetMessageId, String targetMessageHash, int index, int messageFlags) {
        return this.taskQueueHelper.submitTask(mjTask, () -> this.discordService.variation(targetMessageId, index, targetMessageHash, messageFlags));
    }

    @Override
    public ApiResult<Void> submitDescribe(MjTask mjTask, DataUrl dataUrl) {
        return this.taskQueueHelper.submitTask(mjTask, () -> {
            String taskFileName = mjTask.getId() + "." + MimeTypeUtils.guessFileSuffix(dataUrl.getMimeType());
            ApiResult<String> uploadResult = this.discordService.upload(taskFileName, dataUrl);
            if (uploadResult.getStatus() != ResultEnum.SUCCESS.getCode()) {
                throw new BussinessException(uploadResult.getMessage());
            }
            String finalFileName = uploadResult.getData();
            return this.discordService.describe(finalFileName);
        });
    }

    @Override
    public ApiResult<Void> submitBlend(MjTask mjTask, List<DataUrl> dataUrls, BlendDimensions dimensions) {
        return this.taskQueueHelper.submitTask(mjTask, () -> {
            List<String> finalFileNames = new ArrayList<>();
            for (DataUrl dataUrl : dataUrls) {
                String taskFileName = mjTask.getId() + "." + MimeTypeUtils.guessFileSuffix(dataUrl.getMimeType());
                ApiResult<String> uploadResult = this.discordService.upload(taskFileName, dataUrl);
                if (uploadResult.getStatus() != ResultEnum.SUCCESS.getCode()) {
                    throw new BussinessException(uploadResult.getMessage());
                }
                finalFileNames.add(uploadResult.getData());
            }
            return this.discordService.blend(finalFileNames, dimensions);
        });
    }
}