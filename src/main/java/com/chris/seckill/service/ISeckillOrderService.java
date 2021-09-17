package com.chris.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chris.seckill.pojo.SeckillOrder;
import com.chris.seckill.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 2021-07-28
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    Long getResult(User user, Long goodsId);
}
