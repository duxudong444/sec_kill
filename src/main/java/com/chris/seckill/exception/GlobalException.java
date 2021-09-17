package com.chris.seckill.exception;

import com.chris.seckill.vo.RespBeanEnum;
import com.chris.seckill.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 全局异常
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalException extends RuntimeException {
	private RespBeanEnum respBeanEnum;
}