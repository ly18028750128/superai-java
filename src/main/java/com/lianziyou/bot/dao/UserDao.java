package com.lianziyou.bot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lianziyou.bot.model.User;
import com.lianziyou.bot.model.req.sys.admin.UserQueryPageReq;
import com.lianziyou.bot.model.res.sys.admin.AdminHomeRes;
import com.lianziyou.bot.model.res.sys.admin.UserQueryPageRes;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserDao extends BaseMapper<User> {

    int insertSelective(User record);

    int updateByPrimaryKeySelective(User record);

    int updateBatch(List<User> list);

    int updateBatchSelective(List<User> list);

    User checkTempUser(@Param("browserFingerprint") String browserFingerprint, @Param("ipAddress") String ipAddress);

    Page<UserQueryPageRes> queryUserPage(@Param("page") Page<UserQueryPageRes> page, @Param("req") UserQueryPageReq req);

    AdminHomeRes adminHome();

    User getOne(@Param("fromUser") String fromUser, @Param("mobile") String mobile);

    int increaseRemainingTimes(@Param("userId") Long userId, @Param("addCount") Integer addCount);
}