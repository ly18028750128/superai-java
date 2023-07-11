package com.lianziyou.bot.service.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.dao.AnnouncementDao;
import com.lianziyou.bot.model.Announcement;
import com.lianziyou.bot.model.base.BaseDeleteEntity;
import com.lianziyou.bot.model.req.sys.admin.AnnouncementAddReq;
import com.lianziyou.bot.model.req.sys.admin.AnnouncementQueryReq;
import com.lianziyou.bot.model.req.sys.admin.AnnouncementUpdateReq;
import com.lianziyou.bot.model.res.sys.admin.AnnouncementQueryRes;
import com.lianziyou.bot.service.sys.IAnnouncementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("AnnouncementService")
@Transactional(rollbackFor = Exception.class)
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementDao, Announcement> implements IAnnouncementService {


    @Override
    public ApiResult<Page<AnnouncementQueryRes>> queryPage(AnnouncementQueryReq req) {
        Page<AnnouncementQueryRes> page = new Page<>(req.getPageNumber(), req.getPageSize());
        return ApiResult.okBuild(this.baseMapper.queryAnnouncement(page, req.getTitle()));
    }

    @Override
    public ApiResult<Void> add(AnnouncementAddReq req) {
        Announcement announcement = BeanUtil.copyProperties(req, Announcement.class);
        Long count = this.lambdaQuery()
            .eq(null != announcement.getTitle(), Announcement::getTitle, announcement.getTitle())
            .count();
        if (count > 0) {
            return ApiResult.finalBuild("公告已存在");
        }
        this.save(announcement);
        return ApiResult.okBuild();
    }


    @Override
    public ApiResult<Void> update(AnnouncementUpdateReq req) {
        Announcement announcement = BeanUtil.copyProperties(req, Announcement.class);
        if (null == announcement || null == announcement.getId()) {
            return ApiResult.finalBuild("参数校验失败");
        }
        Long count = this.lambdaQuery()
            .eq(null != announcement.getTitle(), Announcement::getTitle, announcement.getTitle())
            .ne(Announcement::getId, announcement.getId())
            .count();
        if (count > 0) {
            return ApiResult.finalBuild("公告已存在");
        }
        this.saveOrUpdate(announcement);
        return ApiResult.okBuild();
    }


    @Override
    public ApiResult<Void> delete(BaseDeleteEntity params) {
        this.removeByIds(params.getIds());
        return ApiResult.okBuild();
    }
}
