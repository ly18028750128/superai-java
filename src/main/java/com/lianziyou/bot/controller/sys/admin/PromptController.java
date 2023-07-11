package com.lianziyou.bot.controller.sys.admin;

import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.service.prompt.GetPromptScheduledService;
import com.lianziyou.bot.service.sys.AiPromptService;
import com.lianziyou.bot.utils.OpenAiUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author Administrator
 */
@RestController
@RequestMapping("/sys/prompt")
@Api(tags = "后台:Ai提示词相关API", value = "后台:Ai提示词相关API")
public class PromptController {


    @Resource
    AiPromptService aiPromptService;

    @Resource
    GetPromptScheduledService promptScheduledService;


    @ApiOperation("执行获取提示词任务")
    @RequestMapping(value = "/get", name = "执行获取任务", method = RequestMethod.GET)
    public ApiResult<Void> queryPage() {
        promptScheduledService.execute();
        return ApiResult.okBuild();
    }

    @ApiOperation("OpenAI提示词翻译")
    @RequestMapping(value = "/test", name = "OpenAI提示词翻译", method = RequestMethod.GET)
    public ApiResult<String> test(@RequestParam("prompt") String prompt) {
        return ApiResult.okBuild(OpenAiUtil.single().getMjPrompt(prompt));
    }


}
