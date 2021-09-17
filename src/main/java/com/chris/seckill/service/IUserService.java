package com.chris.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chris.seckill.pojo.User;
import com.chris.seckill.vo.LoginVo;
import com.chris.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 2021-07-23
 */
public interface IUserService extends IService<User> {
    /**
     * 功能描述: 登录
     */
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    /**
     * 功能描述: 根据cookie获取用户
     */
    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);

    /**
     * 功能描述:更新密码
     * @param userTicket
     * @param password
     * @param request
     * @param response
     * @return
     */
    RespBean updatePassword(String userTicket, String password, HttpServletRequest request,
                            HttpServletResponse response);
}
