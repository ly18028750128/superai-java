package com.lianziyou.bot.service.draw;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lianziyou.bot.constant.AiDrawConst.TopicalType;
import com.lianziyou.bot.model.UserAiDrawTopical;
import com.lianziyou.bot.vo.DrawTopicalQueryVO;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;

public interface UserAiDrawTopicalService extends IService<UserAiDrawTopical> {


    int insertSelective(UserAiDrawTopical record);

    int updateByPrimaryKeySelective(UserAiDrawTopical record);

    int updateBatch(List<UserAiDrawTopical> list);

    int updateBatchSelective(List<UserAiDrawTopical> list);

    List<UserAiDrawTopical> queryUserDrawTopical(DrawTopicalQueryVO queryParam);

    Boolean removeUserAiDrawTopical(Long id) throws Exception;

}
