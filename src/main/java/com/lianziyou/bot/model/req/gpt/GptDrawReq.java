package com.lianziyou.bot.model.req.gpt;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Administrator
 */
@Data
@ApiModel("GPT绘画")
public class GptDrawReq {

    @NotBlank(message = "消息数据不能为空")
    @ApiModelProperty("提示词")
    private String prompt;

    /**
     * model
     */
    @ApiModelProperty("提示词")
    private String model = "image-alpha-001";

    /**
     * imageSize
     */
    private String size = "512x512";

    /**
     * numberOfImages
     */
    private Integer num_images = 1;

    /**
     * imageType
     */
    private String response_format = "url";

    private Integer type = 3;
}
