package com.lianziyou.bot.constant;

import lombok.AllArgsConstructor;

public interface AiPromptConst {

    String FLOW_GPT = "FLOWGPT";

    String PLEX_PT = "PLEXPT";


    @AllArgsConstructor
    enum PromptSource {
        SYS("系统自己创建的"),
        FLOWGPT("https://flowgpt.com/"),

        PLEXPT("https://github.com/PlexPt/awesome-chatgpt-prompts-zh"),
        ;
        public final String description; // 标签获取的URL
    }

    //    @ApiModelProperty(value = "类型：GPT/MJ/SD")
    @AllArgsConstructor
    enum PromptType {
        GPT("chat gpt"),
        MJ("mid journey"),

        SD("stable diffusion"),
        ;
        public final String description; // 标签获取的URL
    }

}
