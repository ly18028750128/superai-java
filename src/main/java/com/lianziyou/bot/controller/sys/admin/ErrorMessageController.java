package com.lianziyou.bot.controller.sys.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.model.base.BasePageHelper;
import com.lianziyou.bot.model.res.sys.admin.ErrorMessageQueryRes;
import com.lianziyou.bot.service.sys.IErrorMessageService;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/sys/error/message")
public class ErrorMessageController {


    @Resource
    IErrorMessageService errorMessageService;

    @RequestMapping(value = "/queryPage", name = "查询异常日志", method = RequestMethod.POST)
    public ApiResult<Page<ErrorMessageQueryRes>> queryPage(@Validated @RequestBody BasePageHelper req) {
        return errorMessageService.queryPage(req);
    }

}
