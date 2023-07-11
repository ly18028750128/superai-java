package com.lianziyou.bot.model.req.sys;

import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageLogSave implements Serializable {


    private String type;

    private String prompt;

    private List<String> imgList;

    private String startTime;

    private String endTime;

    private String message;

    private Boolean success;
}
