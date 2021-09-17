package com.chris.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chris.seckill.pojo.Goods;
import com.chris.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhoubin
 * @since 2021-07-28
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 功能描述: 获取商品列表
     *
     * @param:
     * @return:
     */
    List<GoodsVo> findGoodsVo();


    /**
     * 功能描述: 获取商品详情
     * @param goodsId
     */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
