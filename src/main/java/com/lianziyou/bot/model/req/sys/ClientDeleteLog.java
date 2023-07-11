package com.lianziyou.bot.model.req.sys;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClientDeleteLog {

    /**
     * 日志id
     */
    @NotNull
    private Long id;
}
