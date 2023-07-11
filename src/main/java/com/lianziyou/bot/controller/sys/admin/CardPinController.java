package com.lianziyou.bot.controller.sys.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.model.base.BaseDeleteEntity;
import com.lianziyou.bot.model.base.BasePageHelper;
import com.lianziyou.bot.model.req.sys.admin.CardPinAddReq;
import com.lianziyou.bot.model.res.sys.admin.CardPinQueryRes;
import com.lianziyou.bot.service.sys.ICardPinService;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/sys/card/pin")
public class CardPinController {


    @Resource
    ICardPinService cardPinService;

    @RequestMapping(value = "/queryPage", name = "查询卡密", method = RequestMethod.POST)
    public ApiResult<Page<CardPinQueryRes>> queryPage(@Validated @RequestBody BasePageHelper req) {
        return cardPinService.queryPage(req);
    }

    @RequestMapping(value = "/add", name = "新增卡密", method = RequestMethod.POST)
    public ApiResult<Void> add(@Validated @RequestBody CardPinAddReq req) {
        return cardPinService.add(req);
    }


    @RequestMapping(value = "/delete", name = "删除卡密", method = RequestMethod.POST)
    public ApiResult<Void> delete(@Validated @RequestBody BaseDeleteEntity req) {
        return cardPinService.delete(req);
    }

    @RequestMapping(value = "/getAllCardPin", name = "获取有效的卡密列表", method = RequestMethod.POST)
    public ApiResult<List<String>> getAllCardPin() {
        return cardPinService.getAllCardPin();
    }

}
