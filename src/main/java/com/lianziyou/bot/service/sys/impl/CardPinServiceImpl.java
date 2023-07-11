package com.lianziyou.bot.service.sys.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.dao.CardPinDao;
import com.lianziyou.bot.model.CardPin;
import com.lianziyou.bot.model.base.BaseDeleteEntity;
import com.lianziyou.bot.model.base.BasePageHelper;
import com.lianziyou.bot.model.req.sys.admin.CardPinAddReq;
import com.lianziyou.bot.model.res.sys.admin.CardPinQueryRes;
import com.lianziyou.bot.service.sys.ICardPinService;
import com.lianziyou.bot.utils.sys.PasswordUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("CardPinService")
@Log4j2
@Transactional(rollbackFor = Exception.class)
public class CardPinServiceImpl extends ServiceImpl<CardPinDao, CardPin> implements ICardPinService {


    @Override
    public ApiResult<Page<CardPinQueryRes>> queryPage(BasePageHelper req) {
        Page<CardPinQueryRes> page = new Page<>(req.getPageNumber(), req.getPageSize());
        return ApiResult.okBuild(this.baseMapper.queryCardPin(page));
    }

    @Override
    public ApiResult<Void> add(CardPinAddReq req) {
        CardPin cardPin = null;
        List<CardPin> list = new ArrayList<>();
        for (int i = 0; i < req.getBatch(); i++) {
            cardPin = new CardPin();
            cardPin.setNumber(req.getNumber());
            cardPin.setCardPin(PasswordUtil.getRandomPassword());
            list.add(cardPin);
        }
        if (list.size() > 0) {
            this.saveBatch(list);
        }
        return ApiResult.okBuild();
    }

    @Override
    public ApiResult<Void> delete(BaseDeleteEntity req) {
        this.removeByIds(req.getIds());
        return ApiResult.okBuild();
    }

    @Override
    public ApiResult<List<String>> getAllCardPin() {
        List<CardPin> list = this.lambdaQuery().eq(CardPin::getState, 0).select(CardPin::getCardPin).list();
        List<String> cardPinList = null == list ? new ArrayList<>() : list.stream().map(CardPin::getCardPin).collect(Collectors.toList());
        return ApiResult.okBuild(cardPinList);
    }
}
