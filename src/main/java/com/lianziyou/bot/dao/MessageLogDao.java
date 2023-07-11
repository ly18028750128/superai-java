package com.lianziyou.bot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lianziyou.bot.model.MessageLog;
import com.lianziyou.bot.model.req.gpt.ChatUpdateReq;
import java.util.List;

public interface MessageLogDao extends BaseMapper<MessageLog> {

    int insertSelective(MessageLog record);

    int updateByPrimaryKeySelective(MessageLog record);

    int updateBatch(List<MessageLog> list);

    int updateBatchSelective(List<MessageLog> list);

    int updateLog(ChatUpdateReq req);
}