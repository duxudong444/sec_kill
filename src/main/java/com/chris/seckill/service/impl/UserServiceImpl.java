package com.chris.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chris.seckill.exception.GlobalException;
import com.chris.seckill.mapper.UserMapper;
import com.chris.seckill.pojo.User;
import com.chris.seckill.service.IUserService;
import com.chris.seckill.utils.CookieUtil;
import com.chris.seckill.utils.MD5Util;
import com.chris.seckill.utils.UUIDUtil;
import com.chris.seckill.utils.ValidatorUtil;
import com.chris.seckill.vo.LoginVo;
import com.chris.seckill.vo.RespBean;
import com.chris.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 2021-07-23
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 功能描述: 登录
     *
     * @param:
     * @return: 乐字节：专注线上IT培训
     * 答疑老师微信：lezijie
     * @since: 1.0.0
     * @Author: zhoubin
     */
    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        // //参数校验
//         if (StringUtils.isEmpty(mobile)||StringUtils.isEmpty(password)){
//         	return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//         }
//         if (!ValidatorUtil.isMobile(mobile)){
//         	return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//         }
        //根据手机号获取用户
        User user = userMapper.selectById(mobile);
        if (null == user) {
//            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        //判断密码是否正确
        if (!MD5Util.formPassToDBPass(password, user.getSlat()).equals(user.getPassword())) {
//            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        //生成cookie
        String ticket = UUIDUtil.uuid();
//        //将用户信息存入redis中
        redisTemplate.opsForValue().set("user:" + ticket, user);
//         request.getSession().setAttribute(ticket,user);
        CookieUtil.setCookie(request, response, "userTicket", ticket);
        return RespBean.success(ticket);
    }


    /**
     * 功能描述: 根据cookie获取用户
     *
     */
    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(userTicket)) {
            return null;
        }
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        if (user != null) {
            //以防萬一
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }
        return user;
    }



    /**
     * 功能描述:
     *
     * @param: 更新密码
     */
    @Override
    public RespBean updatePassword(String userTicket, String password, HttpServletRequest request,
                                   HttpServletResponse response) {
        User user = getUserByCookie(userTicket, request, response);
        if (user == null) {
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        user.setPassword(MD5Util.inputPassToDBPass(password, user.getSlat()));
        int result = userMapper.updateById(user);
        if (1 == result) {
            //删除Redis
            redisTemplate.delete("user:" + userTicket);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }

}
