package com.lianziyou.bot.model.req.sys;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClientEmptyLog {

    /**
     * 消息类型  1-gpt对话 2-gpt画图 3-sd画图 4-fs画图 5-mj画图 6-bing对话
     */
    @NotNull
    private Integer sendType;
}
