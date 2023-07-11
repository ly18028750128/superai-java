package com.lianziyou.bot.controller.sys;


import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.model.Order;
import com.lianziyou.bot.model.req.sys.CreateOrderReq;
import com.lianziyou.bot.model.req.sys.CreatePinOrderReq;
import com.lianziyou.bot.model.req.sys.OrderYiCallBackReq;
import com.lianziyou.bot.service.sys.IOrderService;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    IOrderService orderService;

    @RequestMapping(value = "/yi/create", name = "易支付订单创建", method = RequestMethod.POST)
    public ApiResult<Order> createYiOrder(@Validated @RequestBody CreateOrderReq req) throws Throwable {
        Order order = orderService.createOrder(req);
        return ApiResult.okBuild(order);
    }


    @RequestMapping(value = "/yi/callback", name = "易支付支付回调", method = RequestMethod.GET)
    public String yiCallback(OrderYiCallBackReq req) {
        return orderService.yiCallback(req);
    }

    @RequestMapping(value = "/card/pin", name = "卡密支付", method = RequestMethod.POST)
    public ApiResult<Void> cardPin(@Validated @RequestBody CreatePinOrderReq req) {
        return orderService.cardPin(req);
    }

}
