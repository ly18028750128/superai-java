package com.lianziyou.bot.model.req.gpt;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("GPT对话请求")
public class GptStreamReq {

    @ApiModelProperty("提示词")
    private String problem;

    @ApiModelProperty("会话ID，如果为空就新建会话")
    private Long logId;

    @ApiModelProperty("会话类型，默认为3，GPT3.5")
    private Integer type = 3;

    @ApiModelProperty("初始化的提示词ID，来自t_ai_prompt表，仅第一次提交时有效，以后都通过log表获取")
    private Long initPromptId;  // 初始化的提示词ID，来自t_ai_prompt表

}
