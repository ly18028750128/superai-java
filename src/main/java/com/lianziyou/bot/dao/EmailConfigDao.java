package com.lianziyou.bot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lianziyou.bot.model.EmailConfig;
import com.lianziyou.bot.model.res.sys.admin.EmailConfigQueryRes;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface EmailConfigDao extends BaseMapper<EmailConfig> {

    int insertSelective(EmailConfig record);

    int updateByPrimaryKeySelective(EmailConfig record);

    int updateBatch(List<EmailConfig> list);

    int updateBatchSelective(List<EmailConfig> list);

    Page<EmailConfigQueryRes> queryEmailConfig(Page<EmailConfigQueryRes> page, @Param("username") String username);
}