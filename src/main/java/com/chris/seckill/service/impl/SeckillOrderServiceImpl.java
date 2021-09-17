package com.chris.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chris.seckill.mapper.SeckillOrderMapper;
import com.chris.seckill.pojo.SeckillOrder;
import com.chris.seckill.pojo.User;
import com.chris.seckill.service.ISeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 2021-07-28
 */
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Override
    public Long getResult(User user, Long goodsId) {
         SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId())
                .eq("goods_id", goodsId));
        if (null != seckillOrder){
            return seckillOrder.getOrderId();
            //判断是不是空
        }else if (redisTemplate.hasKey("isStockEmpty:"+goodsId)){
            return -1L;
        }else
            return 0L;
    }
}
