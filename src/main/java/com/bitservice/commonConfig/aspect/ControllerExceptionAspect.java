package com.bitservice.commonConfig.aspect;

import com.alibaba.fastjson.JSONException;
import com.bitservice.common.web.RestResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author hrong
 **/
@Aspect
@Component
public class ControllerExceptionAspect {
	private static Logger log = LoggerFactory.getLogger(ControllerExceptionAspect.class);

	@Pointcut(value = "execution(public * com.bitservice..*Controller.*(..))")
	public void controllerMethod() {
	}

	@Around("controllerMethod()")
	public Object handlerControllerMethod(ProceedingJoinPoint pjp) {
		long startTime = System.currentTimeMillis();
		Object result;
		try {
			result = pjp.proceed();
			log.info(pjp.getSignature() + "花费时间:" + (System.currentTimeMillis() - startTime));
		} catch (Throwable e) {

			result = handlerException(pjp, e);
		}
		return result;
	}

	private RestResult handlerException(ProceedingJoinPoint pjp, Throwable e) {
		log.error("出现异常！{}>>>>{}",pjp.getSignature(),e.getMessage());
		String msg;
		if (e instanceof JSONException) {
			msg = "json格式化出现异常：" + e.getMessage();
		} else if (e instanceof IOException) {
			msg = "io异常：" + e.getMessage();
		} else {
			msg = "服务器异常:" + e.getMessage();
		}
		return RestResult.error(null, msg);
	}
}
