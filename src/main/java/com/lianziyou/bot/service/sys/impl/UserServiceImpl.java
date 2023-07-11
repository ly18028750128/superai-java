package com.lianziyou.bot.service.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.dao.OrderDao;
import com.lianziyou.bot.dao.UserDao;
import com.lianziyou.bot.model.User;
import com.lianziyou.bot.model.base.BaseDeleteEntity;
import com.lianziyou.bot.model.req.sys.admin.UserQueryPageReq;
import com.lianziyou.bot.model.req.sys.admin.UserUpdateReq;
import com.lianziyou.bot.model.res.sys.admin.AdminHomeOrder;
import com.lianziyou.bot.model.res.sys.admin.AdminHomeOrderPrice;
import com.lianziyou.bot.model.res.sys.admin.AdminHomeRes;
import com.lianziyou.bot.model.res.sys.admin.UserQueryPageRes;
import com.lianziyou.bot.service.sys.IUserService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("userService")
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements IUserService {

    @Resource
    OrderDao orderDao;

    @Override
    public ApiResult<Page<UserQueryPageRes>> queryPage(UserQueryPageReq req) {
        Page<UserQueryPageRes> page = new Page<>(req.getPageNumber(), req.getPageSize());
        return ApiResult.okBuild(this.baseMapper.queryUserPage(page, req));
    }

    @Override
    public ApiResult<Void> update(UserUpdateReq req) {
        User user = BeanUtil.copyProperties(req, User.class);
        if (null != user.getPassword()) {
            user.setPassword(SecureUtil.md5(req.getPassword()));
        }
        this.updateById(user);
        return ApiResult.okBuild();
    }

    @Override
    public ApiResult<Void> delete(BaseDeleteEntity req) {
        this.removeByIds(req.getIds());
        return ApiResult.okBuild();
    }

    @Override
    public User checkTempUser(String browserFingerprint, String ipaddress) {
        return this.baseMapper.checkTempUser(browserFingerprint, ipaddress);
    }

    @Override
    public ApiResult<AdminHomeRes> adminHome() {
        AdminHomeRes adminHomeRes = this.baseMapper.adminHome();
        List<AdminHomeOrder> adminHomeOrderList = orderDao.queryHomeOrder();
        if (null == adminHomeOrderList || adminHomeOrderList.size() == 0) {
            adminHomeOrderList = new ArrayList<>();
        }
        adminHomeRes.setOrderList(adminHomeOrderList);
        List<AdminHomeOrderPrice> adminHomeOrderPrices = orderDao.queryHomeOrderPrice();
        if (null == adminHomeOrderPrices || adminHomeOrderPrices.size() == 0) {
            adminHomeOrderPrices = new ArrayList<>();
        }
        adminHomeRes.setOrderPriceList(adminHomeOrderPrices);
        return ApiResult.okBuild(adminHomeRes);
    }

    @Override
    public User getOne(String fromUser, String mobile) {
        return baseMapper.getOne(fromUser, mobile);
    }
}
