package com.chris.seckill.rabbitmq;

import com.chris.seckill.pojo.SeckillMessage;
import com.chris.seckill.pojo.SeckillOrder;
import com.chris.seckill.pojo.User;
import com.chris.seckill.service.IGoodsService;
import com.chris.seckill.service.IOrderService;
import com.chris.seckill.utils.JsonUtil;
import com.chris.seckill.vo.GoodsVo;
import com.chris.seckill.vo.RespBean;
import com.chris.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 消息的消费者
 */

@Slf4j
@Service
public class MQReceiver {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IOrderService orderService;

//    @RabbitListener(queues = "queue")
//    public void receive(Object msg) {
//        log.info("接收消息：" + msg);
//    }

    /**
     * 下单操作
     * @param message
     */
    @RabbitListener(queues = "seckillQueue")
    public void receive(String message){
        log.info("接收的消息："+message);
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message,SeckillMessage.class);
        Long goodId = seckillMessage.getGoodId();
        User user = seckillMessage.getUser();
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodId);
        //判断库存
        if (goodsVo.getStockCount() < 1){
            return;
        }
        //判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodId);
        if (seckillOrder != null){
            return ;
        }
        //下单操作
        orderService.secKill(user,goodsVo);
    }
}
