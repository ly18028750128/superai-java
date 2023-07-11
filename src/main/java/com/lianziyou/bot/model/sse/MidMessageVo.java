package com.lianziyou.bot.model.sse;

import com.lianziyou.bot.model.req.mj.MjCallBack;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MidMessageVo implements Serializable {

    private Long userId;
    private MjCallBack message;
}
