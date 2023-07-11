package com.lianziyou.bot.service.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.dao.PayConfigDao;
import com.lianziyou.bot.model.PayConfig;
import com.lianziyou.bot.model.req.sys.admin.PayConfigUpdateReq;
import com.lianziyou.bot.model.res.sys.admin.PayConfigQueryRes;
import com.lianziyou.bot.service.sys.IPayConfigService;
import com.lianziyou.bot.utils.sys.RedisUtil;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("PayConfigService")
@Transactional(rollbackFor = Exception.class)
public class PayConfigServiceImpl extends ServiceImpl<PayConfigDao, PayConfig> implements IPayConfigService {

    @Override
    public ApiResult<PayConfigQueryRes> queryPayConfig() {
        PayConfig payConfig = this.lambdaQuery().eq(PayConfig::getId, 1).one();
        return ApiResult.okBuild(BeanUtil.copyProperties(payConfig, PayConfigQueryRes.class));
    }

    @Override
    public ApiResult<Void> saveOrUpdate(PayConfigUpdateReq req) {
        PayConfig payConfig = this.baseMapper.selectOne(null);
        if (payConfig == null) {
            payConfig = new PayConfig();
        }
        BeanUtil.copyProperties(req, payConfig, CommonConst.DEFAULT_COPY_OPTIONS);
        payConfig.setOperateTime(LocalDateTime.now());
        super.saveOrUpdate(payConfig);
        RedisUtil.setCacheObject(CommonConst.PAY_CONFIG, this.getById(payConfig.getId()));
        return ApiResult.okBuild();
    }
}
