package com.chris.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chris.seckill.pojo.Order;
import com.chris.seckill.pojo.User;
import com.chris.seckill.vo.GoodsVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 2021-07-28
 */
public interface IOrderService extends IService<Order> {


    /**
     * 秒杀
     * @param user
     * @param goods
     * @return
     */
    Order secKill(User user, GoodsVo goods);
}
