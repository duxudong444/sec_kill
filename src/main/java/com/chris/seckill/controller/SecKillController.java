package com.chris.seckill.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chris.seckill.pojo.*;
import com.chris.seckill.rabbitmq.MQSender;
import com.chris.seckill.service.IGoodsService;
import com.chris.seckill.service.IOrderService;
import com.chris.seckill.service.ISeckillOrderService;
import com.chris.seckill.utils.JsonUtil;
import com.chris.seckill.vo.GoodsVo;
import com.chris.seckill.vo.RespBean;
import com.chris.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 秒杀
 */
@Controller
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;
    //内存标记，Long对应商品Id,false表示不为空
    private Map<Long,Boolean> emptyStockMap = new HashMap<>();

    /**
     * 秒杀
     * 优化前： Windows QPS:785   LINUX QPS:170
     * 优化后：缓存QPS：1749.4  优化QPS：4000+
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/doSeckill",method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill(Model model, User user, Long goodsId){
        if (user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        //判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null){
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //预减库存（这是带有原子性的）
        Long stock = redisTemplate.opsForValue().decrement("seckillGoods:"+ goodsId);
        if (stock < 0){
            emptyStockMap.put(goodsId,true);
            redisTemplate.opsForValue().increment("seckillGoods:"+ goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //生成订单
        SeckillMessage seckillMessage = new SeckillMessage(user,goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));
        return RespBean.success(0);




/*//        model.addAttribute("user",user);
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if (goods.getStockCount() <1){
//            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //判断是否重复抢购
//        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()
//        ).eq("goods_id", goodsId));
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null){
//            model.addAttribute("errmsg",RespBeanEnum.REPEATE_ERROR.getMessage());
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //生成订单
        Order order = orderService.secKill(user,goods);
        return RespBean.success(order);

*/
    }

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return
     */

    @RequestMapping(value = "/result",method = RequestMethod.GET)
    public RespBean getResult(User user,Long goodsId){
        if (user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user,goodsId);
        return RespBean.success(orderId);
    }

    /**
     * 系统初始化，，把商品库存信息加载到Redis
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(list)){
            return;
        }
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:"+goodsVo.getId(),goodsVo.getStockCount());
            emptyStockMap.put(goodsVo.getId(),false); //标记不为空
        });

    }
}
