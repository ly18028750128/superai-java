package com.lianziyou.bot.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.model.Announcement;
import com.lianziyou.bot.model.base.BaseDeleteEntity;
import com.lianziyou.bot.model.req.sys.admin.AnnouncementAddReq;
import com.lianziyou.bot.model.req.sys.admin.AnnouncementQueryReq;
import com.lianziyou.bot.model.req.sys.admin.AnnouncementUpdateReq;
import com.lianziyou.bot.model.res.sys.admin.AnnouncementQueryRes;


public interface IAnnouncementService extends IService<Announcement> {


    ApiResult<Page<AnnouncementQueryRes>> queryPage(AnnouncementQueryReq req);


    ApiResult<Void> add(AnnouncementAddReq req);


    ApiResult<Void> update(AnnouncementUpdateReq req);


    ApiResult<Void> delete(BaseDeleteEntity params);


}
