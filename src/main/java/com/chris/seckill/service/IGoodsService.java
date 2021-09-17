package com.chris.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chris.seckill.pojo.Goods;
import com.chris.seckill.vo.GoodsVo;
import com.chris.seckill.vo.RespBean;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 2021-07-28
 */
public interface IGoodsService extends IService<Goods> {


    /**
     * 功能描述: 获取商品列表
     *
     * @param:
     * @return:
     *
     */
    List<GoodsVo> findGoodsVo();

    /**
     * 功能描述: 获取商品详情
     *
     * @param:
     * @return:
     */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
