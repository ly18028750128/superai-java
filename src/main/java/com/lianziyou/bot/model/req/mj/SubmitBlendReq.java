package com.lianziyou.bot.model.req.mj;

import com.lianziyou.bot.enums.mj.BlendDimensions;
import java.util.List;
import lombok.Data;

@Data
public class SubmitBlendReq {

    /**
     * 图片base64数组
     */
    private List<String> base64Array;

    /**
     * 比例: PORTRAIT(2:3); SQUARE(1:1); LANDSCAPE(3:2)
     */
    private BlendDimensions dimensions = BlendDimensions.SQUARE;
}
