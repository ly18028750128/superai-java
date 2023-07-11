package com.lianziyou.bot.service.mj;


import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.enums.mj.BlendDimensions;
import com.lianziyou.bot.model.MjTask;
import eu.maxschuster.dataurl.DataUrl;
import java.util.List;

public interface TaskService {

    ApiResult<Void> submitImagine(MjTask mjTask);

    ApiResult<Void> submitUpscale(MjTask mjTask, String targetMessageId, String targetMessageHash, int index, int messageFlags);

    ApiResult<Void> submitVariation(MjTask mjTask, String targetMessageId, String targetMessageHash, int index, int messageFlags);

    ApiResult<Void> submitDescribe(MjTask mjTask, DataUrl dataUrl);

    ApiResult<Void> submitBlend(MjTask mjTask, List<DataUrl> dataUrls, BlendDimensions dimensions);
}