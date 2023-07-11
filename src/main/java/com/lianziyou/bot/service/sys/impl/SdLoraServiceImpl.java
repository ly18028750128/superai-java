package com.lianziyou.bot.service.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianziyou.bot.base.exception.BussinessException;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.dao.SdLoraDao;
import com.lianziyou.bot.model.SdLora;
import com.lianziyou.bot.model.SysConfig;
import com.lianziyou.bot.model.base.BaseDeleteEntity;
import com.lianziyou.bot.model.req.sys.admin.SdLoraAddReq;
import com.lianziyou.bot.model.req.sys.admin.SdLoraQueryReq;
import com.lianziyou.bot.model.req.sys.admin.SdLoraUpdateReq;
import com.lianziyou.bot.model.res.sys.admin.SdLoraQueryRes;
import com.lianziyou.bot.service.sys.ISdLoraService;
import com.lianziyou.bot.utils.sys.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("SdLoraService")
@Log4j2
@Transactional(rollbackFor = BussinessException.class)
public class SdLoraServiceImpl extends ServiceImpl<SdLoraDao, SdLora> implements ISdLoraService {


    @Override
    public ApiResult<Page<SdLoraQueryRes>> queryLoraPage(SdLoraQueryReq req) {
        Page<SdLoraQueryRes> page = new Page<>(req.getPageNumber(), req.getPageSize());
        Page<SdLoraQueryRes> sdLoraQueryResPage = this.baseMapper.queryLoraPage(page, req);
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        sdLoraQueryResPage.getRecords().forEach(s -> {
            s.setImgReturnUrl(cacheObject.getImgReturnUrl());
        });
        return ApiResult.okBuild(sdLoraQueryResPage);
    }

    @Override
    public ApiResult<Void> addLora(SdLoraAddReq req) {
        Long count = this.lambdaQuery().eq(SdLora::getLoraName, req.getLoraName()).count();
        if (count > 0) {
            throw new BussinessException("Lora已存在");
        }
        SdLora sdLora = BeanUtil.copyProperties(req, SdLora.class);
        this.save(sdLora);
        return ApiResult.okBuild();
    }

    @Override
    public ApiResult<Void> updateLora(SdLoraUpdateReq req) {
        Long count = this.lambdaQuery()
            .eq(SdLora::getLoraName, req.getLoraName())
            .ne(SdLora::getId, req.getId())
            .count();
        if (count > 0) {
            throw new BussinessException("Lora已存在");
        }
        SdLora sdLora = BeanUtil.copyProperties(req, SdLora.class);
        this.updateById(sdLora);
        return ApiResult.okBuild();
    }

    @Override
    public ApiResult<Void> deleteLora(BaseDeleteEntity req) {
        this.removeByIds(req.getIds());
        return ApiResult.okBuild();
    }
}
