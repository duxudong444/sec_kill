package com.chris.seckill.controller;


import com.chris.seckill.rabbitmq.MQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhoubin
 * @since 2021-07-23
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    MQSender mqsender;

//    /**
//     * 测试发送MQ的消息
//     */
//    @RequestMapping("/mq")
//    @ResponseBody
//    public void mq(){
//        mqsender.send("hello");
//    }
}
