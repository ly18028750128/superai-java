package com.lianziyou.bot.model.req.mj;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 提交Imagine或UV任务参数
 */
@Data
@ApiModel("MJ绘图提交任务")
public class SubmitReq {

    @ApiModelProperty(value="图片列表")
    List<String> imgList;
    /**
     * prompt: action 为 IMAGINE 必传.
     */
    @NotNull
    @ApiModelProperty(value="prompt: action 为 IMAGINE 必传.")
    private String prompt;
    /**
     * 过滤关键字
     */
    @ApiModelProperty(value="过滤关键字")
    private String no;

    /**
     * 版本
     */
    @ApiModelProperty(value="版本")
    private String version;

    /**
     * 风格化
     */
    @ApiModelProperty(value="风格化")
    private String stylize;

    /**
     * 混乱
     */
    @ApiModelProperty(value="混乱")
    private String chaos;

    /**
     * 品质
     */
    @ApiModelProperty(value="品质")
    private String q;

    /**
     * 尺寸
     */
    @ApiModelProperty(value="尺寸")
    private String ar;

    /**
     * niji风格
     */
    @ApiModelProperty(value="niji风格")
    private String style;

    @ApiModelProperty(value="主题id，关联t_use_ai_draw_topic表")
    private Long topicalId;
}
