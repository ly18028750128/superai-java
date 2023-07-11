package com.lianziyou.bot.enums.wx;

import lombok.AllArgsConstructor;

/**
 * @author Administrator
 */

@AllArgsConstructor
public enum WeixinTradeType {
    NATIVE("native"),
    JSAPI("jsapi"),
    H5("h5"),
    APP("app");

    public final String value;
}
