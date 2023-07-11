package com.lianziyou.bot.controller.sys.admin;

import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.model.req.sys.admin.PayConfigUpdateReq;
import com.lianziyou.bot.model.res.sys.admin.PayConfigQueryRes;
import com.lianziyou.bot.service.sys.IPayConfigService;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/sys/pay/config")
public class PayConfigController {


    @Resource
    IPayConfigService payConfigService;


    @RequestMapping(value = "/query", name = "查询payConfig", method = RequestMethod.POST)
    public ApiResult<PayConfigQueryRes> queryPayConfig() {
        return payConfigService.queryPayConfig();
    }


    @RequestMapping(value = "/update", name = "更新payConfig", method = RequestMethod.POST)
    public ApiResult<Void> update(@Validated @RequestBody PayConfigUpdateReq req) {
        return payConfigService.saveOrUpdate(req);
    }


}
