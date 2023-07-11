package com.lianziyou.bot.model.sse;

import com.lianziyou.bot.model.req.sys.MessageLogSave;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SdMessageVo implements Serializable {

    private Long userId;
    private MessageLogSave message;
}
