package com.lianziyou.bot.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyV3Result.DecryptNotifyResult;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.model.Order;
import com.lianziyou.bot.model.req.sys.CreateOrderReq;
import com.lianziyou.bot.model.req.sys.CreatePinOrderReq;
import com.lianziyou.bot.model.req.sys.OrderYiCallBackReq;
import com.lianziyou.bot.model.req.sys.OrderYiReturnReq;
import com.lianziyou.bot.model.req.sys.admin.OrderQueryReq;
import com.lianziyou.bot.model.res.sys.ClientOrderRes;
import com.lianziyou.bot.model.res.sys.admin.OrderQueryRes;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;


public interface IOrderService extends IService<Order> {


    Order createOrder(CreateOrderReq req) throws Throwable;


    @Transactional(rollbackFor = Throwable.class)
    String wxCallback(DecryptNotifyResult decryptNotifyResult) throws Exception;

    String yiCallback(OrderYiCallBackReq req);


    ApiResult<Void> yiReturnUrl(OrderYiReturnReq req);


    ApiResult<Void> cardPin(CreatePinOrderReq req);


    List<ClientOrderRes> userOrderList(Long userId);


    ApiResult<Page<OrderQueryRes>> query(OrderQueryReq req);


}
