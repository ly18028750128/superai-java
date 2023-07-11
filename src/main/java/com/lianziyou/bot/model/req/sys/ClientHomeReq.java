package com.lianziyou.bot.model.req.sys;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClientHomeReq {

    /**
     * 对话类型
     */
    @NotNull(message = "对话类型不能为空")
    private Integer sendType;
}
