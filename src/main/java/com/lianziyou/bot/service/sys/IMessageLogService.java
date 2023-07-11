package com.lianziyou.bot.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lianziyou.bot.model.MessageLog;
import com.lianziyou.bot.model.gpt.Message;
import com.lianziyou.bot.model.req.gpt.ChatUpdateReq;
import java.util.List;


public interface IMessageLogService extends IService<MessageLog> {


    List<Message> createMessageLogList(Long logId, String problem);

    Boolean updateLog(ChatUpdateReq req);
}
