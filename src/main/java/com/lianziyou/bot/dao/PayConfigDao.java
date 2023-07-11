package com.lianziyou.bot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lianziyou.bot.model.PayConfig;
import java.util.List;

public interface PayConfigDao extends BaseMapper<PayConfig> {

    int insertSelective(PayConfig record);

    int updateByPrimaryKeySelective(PayConfig record);

    int updateBatch(List<PayConfig> list);

    int updateBatchSelective(List<PayConfig> list);
}