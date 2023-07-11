package com.lianziyou.bot.service.sys.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianziyou.bot.dao.AiPromptDao;
import com.lianziyou.bot.model.AiPrompt;
import com.lianziyou.bot.service.sys.AiPromptService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AiPromptServiceImpl extends ServiceImpl<AiPromptDao, AiPrompt> implements AiPromptService {

    @Override
    public int insertSelective(AiPrompt record) {
        return baseMapper.insertSelective(record);
    }

    @Override
    public int updateByPrimaryKeySelective(AiPrompt record) {
        return baseMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateBatch(List<AiPrompt> list) {
        return baseMapper.updateBatch(list);
    }

    @Override
    public int updateBatchSelective(List<AiPrompt> list) {
        return baseMapper.updateBatchSelective(list);
    }

    @Override
    public int insertOrUpdate(AiPrompt record) {
        return baseMapper.insertOrUpdate(record);
    }

    @Override
    public int insertOrUpdateSelective(AiPrompt record) {
        return baseMapper.insertOrUpdateSelective(record);
    }
}

