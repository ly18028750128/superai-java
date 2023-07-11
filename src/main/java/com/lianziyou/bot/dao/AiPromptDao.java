package com.lianziyou.bot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lianziyou.bot.model.AiPrompt;
import java.util.List;

public interface AiPromptDao extends BaseMapper<AiPrompt> {

    int insertSelective(AiPrompt record);

    int updateByPrimaryKeySelective(AiPrompt record);

    int updateBatch(List<AiPrompt> list);

    int updateBatchSelective(List<AiPrompt> list);

    int insertOrUpdate(AiPrompt record);

    int insertOrUpdateSelective(AiPrompt record);
}