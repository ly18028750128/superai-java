package com.lianziyou.bot.service.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyV3Result.DecryptNotifyResult;
import com.lianziyou.bot.base.exception.BussinessException;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.dao.OrderDao;
import com.lianziyou.bot.dao.UserDao;
import com.lianziyou.bot.model.CardPin;
import com.lianziyou.bot.model.Order;
import com.lianziyou.bot.model.PayConfig;
import com.lianziyou.bot.model.Product;
import com.lianziyou.bot.model.User;
import com.lianziyou.bot.model.req.sys.CreateOrderReq;
import com.lianziyou.bot.model.req.sys.CreatePinOrderReq;
import com.lianziyou.bot.model.req.sys.OrderYiCallBackReq;
import com.lianziyou.bot.model.req.sys.OrderYiReturnReq;
import com.lianziyou.bot.model.req.sys.admin.OrderQueryReq;
import com.lianziyou.bot.model.res.sys.ClientOrderRes;
import com.lianziyou.bot.model.res.sys.CreateOrderRes;
import com.lianziyou.bot.model.res.sys.ReturnUrlRes;
import com.lianziyou.bot.model.res.sys.admin.OrderQueryRes;
import com.lianziyou.bot.service.sys.ICardPinService;
import com.lianziyou.bot.service.sys.IOrderService;
import com.lianziyou.bot.service.sys.IProductService;
import com.lianziyou.bot.service.sys.IUserService;
import com.lianziyou.bot.utils.sys.JwtUtil;
import com.lianziyou.bot.utils.sys.RedisUtil;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;


@Service("orderService")
@Transactional(rollbackFor = Exception.class)
@Log4j2
public class OrderServiceImpl extends ServiceImpl<OrderDao, Order> implements IOrderService {

    @Resource
    private IProductService productService;

    @Resource
    private IUserService userService;

    @Resource
    private ICardPinService cardPinService;

    @Resource
    private UserDao userDao;

    public static String createSign(CreateOrderRes res) {
        Map<String, String> sign = new HashMap<>();
        sign.put("pid", res.getPid().toString());
        sign.put("type", res.getType());
        sign.put("out_trade_no", res.getOutTradeNo());
        sign.put("notify_url", res.getNotifyUrl());
        sign.put("return_url", res.getReturnUrl());
        sign.put("name", res.getName());
        sign.put("money", res.getMoney());
        sign = sortByKey(sign);
        //遍历map 转成字符串
        String signStr = "";
        for (Map.Entry<String, String> m : sign.entrySet()) {
            signStr += m.getKey() + "=" + m.getValue() + "&";
        }
        //去掉最后一个 &
        signStr = signStr.substring(0, signStr.length() - 1);
        //最后拼接上KEY
        signStr += res.getKey();
        //转为MD5
        signStr = DigestUtils.md5DigestAsHex(signStr.getBytes());
        return signStr;
    }

    public static <K extends Comparable<? super K>, V> Map<K, V> sortByKey(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();
        map.entrySet().stream()
            .sorted(Map.Entry.<K, V>comparingByKey()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public synchronized Order createOrder(CreateOrderReq req) throws Throwable {
        Product product = productService.getById(req.getProductId());
        Order order = BeanUtil.copyProperties(req, Order.class);
        order.setUserId(JwtUtil.getUserId());
        order.setPrice(product.getPrice().multiply(new BigDecimal(req.getPayNumber())));
        order.setChargeCount(req.getPayNumber() * product.getNumberTimes()); // 保存冲值次数
        order.setProductName(product.getName());
        order.setCreateTime(LocalDateTime.now());
        this.save(order);
        return order;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public String wxCallback(DecryptNotifyResult decryptNotifyResult) throws Exception {
        Order order = this.getById(decryptNotifyResult.getOutTradeNo());
        if (order.getState() == 1) {
            return "success";
        }
        order.setTradeNo(decryptNotifyResult.getTransactionId());
        order.setOperateTime(LocalDateTime.now());
        order.setState("SUCCESS".equals(decryptNotifyResult.getTradeState()) ? 1 : 2);
        this.saveOrUpdate(order);
        okOrder(order);
        return "success";
    }

    @Override
    public String yiCallback(OrderYiCallBackReq req) {
        log.info("支付开始回调，回调参数：{}", req.toString());
        Order order = this.getById(req.getOut_trade_no());
        if (order.getState() == 1) {
            return "success";
        }
        order.setPayType(req.getType());
        order.setTradeNo(req.getTrade_no());
        BigDecimal money = new BigDecimal(req.getMoney());
        PayConfig payConfig = RedisUtil.getCacheObject(CommonConst.PAY_CONFIG);
        if (!payConfig.getPid().equals(req.getPid())) {
            order.setMsg("支付失败,回调商户id异常");
            order.setState(2);
        }
        if (money.compareTo(order.getPrice()) != 0) {
            log.info("支付失败,支付金额异常，支付金额：{},订单金额：{}", money, order.getPrice());
            order.setMsg("支付失败,支付金额异常，支付金额：" + money + "订单金额" + order.getPrice());
            order.setState(2);
        }
        if (req.getTrade_status().equals("TRADE_SUCCESS")) {
            order.setState(1);
            order.setMsg("支付成功");
        } else {
            log.info("支付失败,支付状态：{}", req.getTrade_status());
            order.setMsg("支付失败,支付状态：" + req.getTrade_status());
            order.setState(2);
        }
        order.setOperateTime(LocalDateTime.now());
        this.saveOrUpdate(order);
        okOrder(order);
        return "success";
    }

    @Override
    public ApiResult<Void> yiReturnUrl(OrderYiReturnReq req) {
        Order order = this.getById(req.getOrderId());
        if (order.getState() == 1) {
            throw new BussinessException("订单已完成");
        }
        PayConfig payConfig = RedisUtil.getCacheObject(CommonConst.PAY_CONFIG);
        if (null == payConfig.getPayType() || payConfig.getPayType() != 0) {
            throw new BussinessException("当前支付方式暂未开启");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("act", "order");
        map.put("pid", payConfig.getPid());
        map.put("key", payConfig.getSecretKey());
        map.put("out_trade_no", req.getOrderId());
        String orderPayInfo = HttpUtil.get(payConfig.getApiUrl(), map);
        ReturnUrlRes returnUrlRes = JSONObject.parseObject(orderPayInfo, ReturnUrlRes.class);
        if (returnUrlRes.getCode() != 1) {
            return ApiResult.finalBuild("订单支付异常");
        }
        order.setPayType(returnUrlRes.getType());
        order.setTradeNo(returnUrlRes.getTrade_no());
        BigDecimal money = returnUrlRes.getMoney();
        if (money.compareTo(order.getPrice()) != 0) {
            log.info("支付失败,支付金额异常，支付金额：{},订单金额：{}", money, order.getPrice());
            order.setMsg("支付失败,支付金额异常，支付金额：" + money + "订单金额" + order.getPrice());
            order.setState(2);
        }
        if (returnUrlRes.getStatus() == 1) {
            order.setState(1);
            order.setMsg("支付成功");
        } else {
            log.info("支付失败,支付状态：{}", returnUrlRes.getStatus());
            order.setMsg("支付失败,支付状态：" + returnUrlRes.getStatus());
            order.setState(2);
        }
        if (null != returnUrlRes.getEndtime()) {
            LocalDateTime endTime = LocalDateTime.parse(returnUrlRes.getEndtime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            order.setOperateTime(endTime);
        }
        if (null != returnUrlRes.getAddtime()) {
            LocalDateTime addTime = LocalDateTime.parse(returnUrlRes.getAddtime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            order.setCreateTime(addTime);
        }
        this.saveOrUpdate(order);
        okOrder(order);
        return ApiResult.okBuild();
    }

    @Override
    public ApiResult<Void> cardPin(CreatePinOrderReq req) {
        PayConfig payConfig = RedisUtil.getCacheObject(CommonConst.PAY_CONFIG);
        if (null == payConfig.getPayType() || payConfig.getPayType() != 1) {
            throw new BussinessException("当前支付方式暂未开启");
        }
        Order order = new Order();
        List<CardPin> list = cardPinService.lambdaQuery().eq(CardPin::getCardPin, req.getCardPin()).list();
        if (null == list || list.size() == 0 || list.get(0).getState() == 1) {
            order.setMsg("无效卡密");
            order.setState(2);
        } else {
            order.setMsg("兑换成功");
            order.setState(1);
        }
        order.setUserId(JwtUtil.getUserId());
        order.setPayNumber(1);
        order.setPayType("卡密兑换");
        order.setTradeNo(req.getCardPin());
        this.save(order);
        if (order.getState() == 1) {
            userService.lambdaUpdate()
                .eq(User::getId, JwtUtil.getUserId())
                .setSql("remaining_times = remaining_times +" + list.get(0).getNumber())
                .update();
            cardPinService.lambdaUpdate()
                .eq(CardPin::getCardPin, req.getCardPin())
                .set(CardPin::getState, 1)
                .set(CardPin::getUserId, JwtUtil.getUserId())
                .update();
        }
        return order.getState() == 1 ? ApiResult.okBuild() : ApiResult.finalBuild("兑换失败，无效卡密");
    }

    @Override
    public List<ClientOrderRes> userOrderList(Long userId) {
        return this.baseMapper.userOrderList(userId);
    }

    @Override
    public ApiResult<Page<OrderQueryRes>> query(OrderQueryReq req) {
        Page<OrderQueryRes> page = new Page<>(req.getPageNumber(), req.getPageSize());
        return ApiResult.okBuild(this.baseMapper.queryOrder(page, req));
    }

    @Transactional(rollbackFor = Throwable.class, propagation = Propagation.MANDATORY)
    public void okOrder(Order order) {
        if (order.getState() == 1) {
            Product product = productService.getById(order.getProductId());
            product.setStock(product.getStock() - order.getPayNumber());
            productService.saveOrUpdate(product);
            userDao.increaseRemainingTimes(order.getUserId(), order.getChargeCount());
        }
    }
}
