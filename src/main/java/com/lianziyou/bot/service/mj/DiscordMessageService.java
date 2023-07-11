package com.lianziyou.bot.service.mj;


import com.lianziyou.bot.model.res.mj.GetTaskRes;
import java.io.IOException;

public interface DiscordMessageService {

    GetTaskRes getMjMessages(Long id) throws IOException;
}
