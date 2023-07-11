package com.lianziyou.bot.service.sys.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianziyou.bot.dao.WxLogDao;
import com.lianziyou.bot.model.WxLog;
import com.lianziyou.bot.service.sys.IWxLogService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;


@Service("WxLogService")
@Log4j2
public class WxLogServiceImpl extends ServiceImpl<WxLogDao, WxLog> implements IWxLogService {

}
