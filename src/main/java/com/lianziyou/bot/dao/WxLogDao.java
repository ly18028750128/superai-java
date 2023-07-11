package com.lianziyou.bot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lianziyou.bot.model.WxLog;
import java.util.List;

public interface WxLogDao extends BaseMapper<WxLog> {

    int insertSelective(WxLog record);

    int updateByPrimaryKeySelective(WxLog record);

    int updateBatch(List<WxLog> list);

    int updateBatchSelective(List<WxLog> list);
}