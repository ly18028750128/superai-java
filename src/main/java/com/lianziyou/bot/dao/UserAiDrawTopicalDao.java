package com.lianziyou.bot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lianziyou.bot.model.UserAiDrawTopical;
import java.util.List;

public interface UserAiDrawTopicalDao extends BaseMapper<UserAiDrawTopical> {
    int insertSelective(UserAiDrawTopical record);

    int updateByPrimaryKeySelective(UserAiDrawTopical record);

    int updateBatch(List<UserAiDrawTopical> list);

    int updateBatchSelective(List<UserAiDrawTopical> list);
}