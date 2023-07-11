package com.lianziyou.bot.model.gpt;

import java.util.List;
import lombok.Data;


@Data
public class ChatCompletionResponse {

    private String id;
    private String object;
    private long created;
    private String model;
    private List<ChatChoice> choices;
}
