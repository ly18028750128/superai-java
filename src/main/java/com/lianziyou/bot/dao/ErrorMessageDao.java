package com.lianziyou.bot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lianziyou.bot.model.ErrorMessage;
import com.lianziyou.bot.model.res.sys.admin.ErrorMessageQueryRes;
import java.util.List;

public interface ErrorMessageDao extends BaseMapper<ErrorMessage> {

    int insertSelective(ErrorMessage record);

    int updateByPrimaryKeySelective(ErrorMessage record);

    int updateBatch(List<ErrorMessage> list);

    int updateBatchSelective(List<ErrorMessage> list);

    Page<ErrorMessageQueryRes> queryErrorMessage(Page<ErrorMessageQueryRes> page);
}