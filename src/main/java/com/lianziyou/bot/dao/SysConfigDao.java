package com.lianziyou.bot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lianziyou.bot.model.SysConfig;
import java.util.List;

public interface SysConfigDao extends BaseMapper<SysConfig> {

    int insertSelective(SysConfig record);

    int updateByPrimaryKeySelective(SysConfig record);

    int updateBatch(List<SysConfig> list);

    int updateBatchSelective(List<SysConfig> list);
}