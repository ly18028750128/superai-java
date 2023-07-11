package com.lianziyou.bot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lianziyou.bot.model.Announcement;
import com.lianziyou.bot.model.res.sys.admin.AnnouncementQueryRes;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AnnouncementDao extends BaseMapper<Announcement> {

    int insertSelective(Announcement record);

    int updateByPrimaryKeySelective(Announcement record);

    int updateBatch(List<Announcement> list);

    int updateBatchSelective(List<Announcement> list);

    Page<AnnouncementQueryRes> queryAnnouncement(Page<AnnouncementQueryRes> page, @Param("title") String title);
}