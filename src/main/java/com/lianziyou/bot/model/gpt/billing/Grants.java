package com.lianziyou.bot.model.gpt.billing;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;


@Data
public class Grants {

    private String object;
    @JsonProperty("data")
    private List<Datum> data;

}
