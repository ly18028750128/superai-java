package com.lianziyou.bot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lianziyou.bot.model.CardPin;
import com.lianziyou.bot.model.res.sys.admin.CardPinQueryRes;
import java.util.List;

public interface CardPinDao extends BaseMapper<CardPin> {

    int insertSelective(CardPin record);

    int updateByPrimaryKeySelective(CardPin record);

    int updateBatch(List<CardPin> list);

    int updateBatchSelective(List<CardPin> list);

    Page<CardPinQueryRes> queryCardPin(Page<CardPinQueryRes> page);
}