package com.lianziyou.bot.model.req.sys;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TempUserAuthReq {

    /**
     * 用户浏览器指纹
     */
    @NotNull
    private String browserFingerprint;

    /**
     * ip地址
     */
    private String ipAddress;

    /**
     * 授权token
     */
    @NotNull
    private String authToken;
}
