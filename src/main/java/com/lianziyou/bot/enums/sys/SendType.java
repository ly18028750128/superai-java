package com.lianziyou.bot.enums.sys;

public enum SendType {

    GPT(1, "gpt"),
    GPT_OFFICIAL(2, "gpt-official"),
    SD(3, "sd"),

    FS(4, "fs"),

    MJ(5, "mj"),
    BING(6, "bing"),
    STABLE_STUDIO(7, "stableStudio"),
    GPT_4(8, "gpt-4");


    private Integer type;

    private String remark;

    SendType(Integer type, String remark) {
        this.type = type;
        this.remark = remark;
    }

    public Integer getType() {
        return this.type;
    }

    public String getRemark() {
        return this.remark;
    }

}
