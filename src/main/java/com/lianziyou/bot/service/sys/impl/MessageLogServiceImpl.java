package com.lianziyou.bot.service.sys.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianziyou.bot.dao.MessageLogDao;
import com.lianziyou.bot.model.MessageLog;
import com.lianziyou.bot.model.gpt.Message;
import com.lianziyou.bot.model.req.gpt.ChatUpdateReq;
import com.lianziyou.bot.service.sys.IMessageLogService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("UseLogService")
@Transactional(rollbackFor = Exception.class)
public class MessageLogServiceImpl extends ServiceImpl<MessageLogDao, MessageLog> implements IMessageLogService {

    @Override
    public List<Message> createMessageLogList(Long logId, String problem) {
        List<Message> messages = new ArrayList<>();
        if (null != logId) {
            MessageLog messageLog = this.getById(logId);
            if (null != messageLog) {
                messages = JSONObject.parseArray(messageLog.getUseValue(), Message.class);
            }
        }
        messages.add(Message.of(problem));
        return messages;
    }

    @Override
    public Boolean updateLog(ChatUpdateReq req) {
        return this.baseMapper.updateLog(req) > 0;
    }
}
