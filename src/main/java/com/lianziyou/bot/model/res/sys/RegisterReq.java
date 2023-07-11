package com.lianziyou.bot.model.res.sys;

import lombok.Data;
import lombok.NonNull;


@Data
public class RegisterReq {


    /**
     * 姓名
     */
    @NonNull
    private String name;


    /**
     * 手机号
     */
    @NonNull
    private String mobile;

    /**
     * 密码
     */
    @NonNull
    private String password;

    /**
     * 浏览器指纹
     */
    private String browserFingerprint;
}
