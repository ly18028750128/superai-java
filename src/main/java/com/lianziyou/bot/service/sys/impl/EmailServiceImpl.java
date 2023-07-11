package com.lianziyou.bot.service.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianziyou.bot.base.exception.BussinessException;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.dao.EmailConfigDao;
import com.lianziyou.bot.model.EmailConfig;
import com.lianziyou.bot.model.base.BaseDeleteEntity;
import com.lianziyou.bot.model.req.sys.admin.EmailConfigAddReq;
import com.lianziyou.bot.model.req.sys.admin.EmailConfigQueryReq;
import com.lianziyou.bot.model.req.sys.admin.EmailConfigUpdateReq;
import com.lianziyou.bot.model.res.sys.admin.EmailConfigQueryRes;
import com.lianziyou.bot.service.sys.IEmailService;
import com.lianziyou.bot.utils.sys.RedisUtil;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("EmailService")
@Log4j2
@Transactional(rollbackFor = Exception.class)
public class EmailServiceImpl extends ServiceImpl<EmailConfigDao, EmailConfig> implements IEmailService {


    @Resource
    RedisUtil redisUtil;

    @Override
    public ApiResult<Page<EmailConfigQueryRes>> queryPage(EmailConfigQueryReq req) {
        Page<EmailConfigQueryRes> page = new Page<>(req.getPageNumber(), req.getPageSize());
        return ApiResult.okBuild(this.baseMapper.queryEmailConfig(page, req.getUsername()));
    }

    @Override
    public ApiResult<Void> add(EmailConfigAddReq req) {
        EmailConfig emailConfig = BeanUtil.copyProperties(req, EmailConfig.class);
        Long count = this.lambdaQuery().eq(EmailConfig::getUsername, emailConfig.getUsername()).count();
        if (count > 0) {
            throw new BussinessException("邮箱已存在");
        }
        this.save(emailConfig);
        List<EmailConfig> emailConfigList = this.list();
        redisUtil.setCacheObject("emailList", emailConfigList);
        return ApiResult.okBuild();
    }

    @Override
    public ApiResult<Void> update(EmailConfigUpdateReq req) {
        EmailConfig emailConfig = BeanUtil.copyProperties(req, EmailConfig.class);
        Long count = this.lambdaQuery().
            eq(EmailConfig::getUsername, emailConfig.getUsername())
            .ne(EmailConfig::getId, emailConfig.getId())
            .count();
        if (count > 0) {
            throw new BussinessException("邮箱已存在");
        }
        this.saveOrUpdate(emailConfig);
        List<EmailConfig> emailConfigList = this.list();
        redisUtil.setCacheObject("emailList", emailConfigList);
        return ApiResult.okBuild();
    }

    @Override
    public ApiResult<Void> delete(BaseDeleteEntity req) {
        this.removeByIds(req.getIds());
        List<EmailConfig> emailConfigList = this.list();
        redisUtil.setCacheObject("emailList", emailConfigList);
        return ApiResult.okBuild();
    }
}
