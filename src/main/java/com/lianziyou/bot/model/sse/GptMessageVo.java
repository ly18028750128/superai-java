package com.lianziyou.bot.model.sse;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GptMessageVo implements Serializable {

    private Long userId;
    private Object message;
}
