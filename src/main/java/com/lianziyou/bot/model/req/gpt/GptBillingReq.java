package com.lianziyou.bot.model.req.gpt;


import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GptBillingReq {

    @NotBlank(message = "key不能为空")
    private String key;

}
