package com.lianziyou.bot.controller.sys.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.model.base.BaseDeleteEntity;
import com.lianziyou.bot.model.req.sys.admin.SdLoraAddReq;
import com.lianziyou.bot.model.req.sys.admin.SdLoraQueryReq;
import com.lianziyou.bot.model.req.sys.admin.SdLoraUpdateReq;
import com.lianziyou.bot.model.req.sys.admin.SdModelAddReq;
import com.lianziyou.bot.model.req.sys.admin.SdModelQueryReq;
import com.lianziyou.bot.model.req.sys.admin.SdModelUpdateReq;
import com.lianziyou.bot.model.res.sys.admin.SdLoraQueryRes;
import com.lianziyou.bot.model.res.sys.admin.SdModelQueryRes;
import com.lianziyou.bot.service.sys.ISdLoraService;
import com.lianziyou.bot.service.sys.ISdModelService;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/sys/sd/config")
public class SdConfigController {


    @Resource
    ISdModelService sdModelService;

    @Resource
    ISdLoraService sdLoraService;

    @RequestMapping(value = "/query/model/page", name = "查询模型列表", method = RequestMethod.POST)
    public ApiResult<Page<SdModelQueryRes>> queryModelPage(@Validated @RequestBody SdModelQueryReq req) {
        return sdModelService.queryModelPage(req);
    }

    @RequestMapping(value = "/add/model", name = "新增模型", method = RequestMethod.POST)
    public ApiResult<Void> addModel(@Validated @RequestBody SdModelAddReq req) {
        return sdModelService.addModel(req);
    }

    @RequestMapping(value = "/update/model", name = "编辑模型", method = RequestMethod.POST)
    public ApiResult<Void> updateModel(@Validated @RequestBody SdModelUpdateReq req) {
        return sdModelService.updateModel(req);
    }

    @RequestMapping(value = "/delete/model", name = "删除模型", method = RequestMethod.POST)
    public ApiResult<Void> deleteModel(@Validated @RequestBody BaseDeleteEntity req) {
        return sdModelService.deleteModel(req);
    }

    @RequestMapping(value = "/query/lora/page", name = "查询lora列表", method = RequestMethod.POST)
    public ApiResult<Page<SdLoraQueryRes>> queryLoraPage(@Validated @RequestBody SdLoraQueryReq req) {
        return sdLoraService.queryLoraPage(req);
    }

    @RequestMapping(value = "/add/lora", name = "新增lora", method = RequestMethod.POST)
    public ApiResult<Void> addLora(@Validated @RequestBody SdLoraAddReq req) {
        return sdLoraService.addLora(req);
    }

    @RequestMapping(value = "/update/lora", name = "编辑lora", method = RequestMethod.POST)
    public ApiResult<Void> updateLora(@Validated @RequestBody SdLoraUpdateReq req) {
        return sdLoraService.updateLora(req);
    }

    @RequestMapping(value = "/delete/lora", name = "删除lora", method = RequestMethod.POST)
    public ApiResult<Void> deleteLora(@Validated @RequestBody BaseDeleteEntity req) {
        return sdLoraService.deleteLora(req);
    }
}
