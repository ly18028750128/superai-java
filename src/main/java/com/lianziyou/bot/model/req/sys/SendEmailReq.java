package com.lianziyou.bot.model.req.sys;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendEmailReq {

    /**
     * 邮箱地址
     */
    @NotNull(message = "邮箱地址不能为空")
    private String email;

    /**
     * 手机号
     */
    @NotNull(message = "手机号不能为空")
    private String mobile;
}
