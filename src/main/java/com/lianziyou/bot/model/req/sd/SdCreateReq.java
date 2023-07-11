package com.lianziyou.bot.model.req.sd;

import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SdCreateReq {


    /**
     * 正面提示词
     */
    @NotNull(message = "正面提示词不能为空")
    private String prompt;

    /**
     * 负面提示词
     */
    private String negativePrompt;

    /**
     * 步数
     */
    @NotNull(message = "步数不能为空")
    private Integer steps;

    /**
     * 宽默认512
     */
    private Integer width = 512;

    /**
     * 高默认512
     */
    private Integer height = 512;

    /**
     * 提示词相关性默认7
     */
    private Integer cfgScale = 7;

    /**
     * 生成图片数量默认1
     */
    @Max(value = 4)
    private Integer batchSize = 1;

    /**
     * 随机种子1
     */
    private Long seed = -1L;

    /**
     * 采样方法
     */
    private String samplerIndex = "Euler a";

    /**
     * 脸部修复
     */
    private Boolean restoreFaces = false;

    /**
     * 模型
     */
    private SdModelCheckpoint overrideSettings;

    /**
     * lora
     */
    private List<String> loraList;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 图生图base64数组
     */
    private List<String> initImages;

    private Boolean success = true;

}

@Data
class SdModelCheckpoint {

    /**
     * 模型
     */
    private String sdModelCheckpoint;

}
