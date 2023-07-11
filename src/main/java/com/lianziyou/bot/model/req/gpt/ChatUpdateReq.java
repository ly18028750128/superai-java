package com.lianziyou.bot.model.req.gpt;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Administrator
 */
@Data
@ApiModel("GPT对话更新")
public class ChatUpdateReq {

    @ApiModelProperty("对话ID")
    private Long id;

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty("对话名称")
    private String name;

    @ApiModelProperty("标签名称")
    private String tagName;


}
