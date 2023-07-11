package com.lianziyou.bot.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lianziyou.bot.model.AiPrompt;
import java.util.List;

public interface AiPromptService extends IService<AiPrompt> {


    int insertSelective(AiPrompt record);

    int updateByPrimaryKeySelective(AiPrompt record);

    int updateBatch(List<AiPrompt> list);

    int updateBatchSelective(List<AiPrompt> list);

    int insertOrUpdate(AiPrompt record);

    int insertOrUpdateSelective(AiPrompt record);
}

