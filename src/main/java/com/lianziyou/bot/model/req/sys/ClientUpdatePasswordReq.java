package com.lianziyou.bot.model.req.sys;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClientUpdatePasswordReq {

    /**
     * 密码
     */
    @NotNull(message = "密码不能为空")
    private String password;
}
