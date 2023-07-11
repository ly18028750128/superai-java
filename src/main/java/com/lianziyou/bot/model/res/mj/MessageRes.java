package com.lianziyou.bot.model.res.mj;


import java.util.List;
import lombok.Data;

@Data
public class MessageRes {

    private Long id;

    private Integer type;

    private String content;

    private List<MessageAttachment> attachments;


}
