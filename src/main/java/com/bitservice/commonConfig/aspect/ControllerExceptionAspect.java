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
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

/**
 * @Author hrong
 **/
@Aspect
@Order(1)
@Component
public class ControllerExceptionAspect {
	private static Logger log = LoggerFactory.getLogger(ControllerExceptionAspect.class);

	@Pointcut(value = "execution(public * com.bitservice..*Controller.*(..))")
	public void controllerMethod() {
	}

	@Around("controllerMethod()")
	public Object handlerControllerMethod(ProceedingJoinPoint pjp) {
		boolean canLog = !isFileRelationController(pjp.getSignature().toLongString());
		boolean notGrid = !isGridController(pjp.getSignature().toLongString());
		String method = pjp.getSignature().toShortString();
		log.info("进入方法：{}", pjp.getSignature());
		if (canLog) {
			log.info("参数：{}", Arrays.toString(pjp.getArgs()));
		}
		long startTime = System.currentTimeMillis();
		Object result;
		try {
			result = pjp.proceed();
			if (canLog && notGrid) {
				log.info("方法：{}处理结果：{}", method, JSONObject.toJSONString(result));
			}
			log.info("方法：{}花费时间:{}", method, (System.currentTimeMillis() - startTime));
			log.info("**********************************************************************");
		} catch (Throwable e) {
			result = handlerException(pjp, e);
		}
		return result;
	}

	private RestResult handlerException(ProceedingJoinPoint pjp, Throwable e) {
		log.error(">>>>出现异常:{}",e.getMessage(), e);
		String msg;
		if (e instanceof JSONException) {
			msg = "json格式化出现异常：" + e.getMessage();
		} else if (e instanceof IOException) {
			msg = "io异常：" + e.getMessage();
		} else if (e instanceof BaseException) {
			//自定义异常信息
			msg = e.getMessage();
		}else if(e instanceof DataAccessException){
			msg = "服务器异常！DB_ERROR";
		} else if (e instanceof IllegalArgumentException) {
			msg = "参数有误：" + e.getMessage();
		} else {
			msg = "服务器异常:" + e.getMessage();
		}
		return RestResult.error(null, msg);
	}

	/**
	 * 校验是否是文件上传controller，防止将参数中的base64打印出来
	 *
	 * @param signature 方法签名
	 * @return 检测结果
	 */
	private boolean isFileRelationController(String signature) {
		return signature.contains("com.bitservice.uploadfile.controller");
	}
	/**
	 * 校验是否是文件上传controller，防止将参数中的base64打印出来
	 *
	 * @param signature 方法签名
	 * @return 检测结果
	 */
	private boolean isGridController(String signature) {
		return signature.contains("com.bitservice.bitsgrid.controller");
	}
}
