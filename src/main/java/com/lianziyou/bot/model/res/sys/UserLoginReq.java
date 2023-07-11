package com.lianziyou.bot.model.res.sys;

import javax.validation.constraints.NotEmpty;
import lombok.Data;


@Data
public class UserLoginReq {

    /**
     * 手机号
     */
    @NotEmpty(message = "手机号不能为空")
    private String mobile;

    /**
     * 密码
     */
    @NotEmpty(message = "密码不能为空")
    private String password;
}
