package com.bitservice.commonConfig.aspect;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bitservice.common.web.RestResult;
import com.bitservice.core.exception.BaseException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

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
		String method = pjp.getSignature().toShortString();
		log.info("进入方法：{}", pjp.getSignature());
		log.info("参数：{}", Arrays.toString(pjp.getArgs()));
		long startTime = System.currentTimeMillis();
		Object result;
		try {
			result = pjp.proceed();
			log.info("方法：{}处理结果：{}", method, JSONObject.toJSONString(result));
			log.info("方法：{}花费时间:{}", method, (System.currentTimeMillis() - startTime));
			log.info("**********************************************************************");
		} catch (Throwable e) {
			result = handlerException(pjp, e);
		}
		return result;
	}

	private RestResult handlerException(ProceedingJoinPoint pjp, Throwable e) {
		log.error("出现异常:", e);
		String msg;
		if (e instanceof JSONException) {
			msg = "json格式化出现异常：" + e.getMessage();
		} else if (e instanceof IOException) {
			msg = "io异常：" + e.getMessage();
		} else if (e instanceof BaseException) {
			//自定义异常信息
			msg = e.getMessage();
		} else{
			msg = "服务器异常:" + e.getMessage();
		}
		return RestResult.error(null, msg);
	}
}
