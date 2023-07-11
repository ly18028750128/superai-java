package com.lianziyou.bot.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.model.CardPin;
import com.lianziyou.bot.model.base.BaseDeleteEntity;
import com.lianziyou.bot.model.base.BasePageHelper;
import com.lianziyou.bot.model.req.sys.admin.CardPinAddReq;
import com.lianziyou.bot.model.res.sys.admin.CardPinQueryRes;
import java.util.List;


public interface ICardPinService extends IService<CardPin> {


    ApiResult<Page<CardPinQueryRes>> queryPage(BasePageHelper req);


    ApiResult<Void> add(CardPinAddReq req);


    ApiResult<Void> delete(BaseDeleteEntity req);

    ApiResult<List<String>> getAllCardPin();


}
