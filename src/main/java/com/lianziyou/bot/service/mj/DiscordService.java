package com.lianziyou.bot.service.mj;


import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.enums.mj.BlendDimensions;
import eu.maxschuster.dataurl.DataUrl;
import java.util.List;

public interface DiscordService {

    ApiResult<Void> imagine(String prompt);

    ApiResult<Void> upscale(String messageId, int index, String messageHash, int messageFlags);

    ApiResult<Void> variation(String messageId, int index, String messageHash, int messageFlags);

    ApiResult<Void> reroll(String messageId, String messageHash, int messageFlags);

    ApiResult<Void> describe(String finalFileName);

    ApiResult<Void> blend(List<String> finalFileNames, BlendDimensions dimensions);

    ApiResult<String> upload(String fileName, DataUrl dataUrl);

    ApiResult<String> sendImageMessage(String content, String finalFileName);

}
