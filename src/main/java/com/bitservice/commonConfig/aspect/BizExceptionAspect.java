package com.bitservice.commonConfig.aspect;

import com.alibaba.fastjson.JSONObject;
import com.bitservice.core.exception.BaseException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

/**
 * @Author wfq
 **/
@Aspect
@Order(2)
@Component
public class BizExceptionAspect {
	private static Logger log = LoggerFactory.getLogger(BizExceptionAspect.class);

	@Pointcut(value = "execution(* com.bitservice..*Service*.*(..))")
	public void bizMethod() {
	}

	@Around("bizMethod()")
	public Object handlerBizMethod(ProceedingJoinPoint pjp) {
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
				String retJSON = JSONObject.toJSONString(result);
				if(retJSON.length() > 10000){
					log.info("方法：{}处理结果：{}", method,retJSON.substring(0,9999)+"... 太长了后面都省略了");
				}else{
					log.info("方法：{}处理结果：{}", method,retJSON);
				}
			}
			log.info("方法：{}花费时间:{}", method, (System.currentTimeMillis() - startTime));
			log.info("**********************************************************************");
		} catch (Throwable e) {
			result = handlerException(pjp, e);
		}
		return result;
	}

	private Throwable handlerException(ProceedingJoinPoint pjp, Throwable e) {
		log.error(">>>>出现异常:{}",getExceptionStackTrace(e), e);
		throw new BaseException(e.getMessage());
		// return e;
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
	public static String getExceptionStackTrace(Throwable anexcepObj)
	{
		StringWriter sw = null;
		PrintWriter printWriter = null;
		try{
			if(anexcepObj != null)
			{
				sw = new StringWriter();
				printWriter = new PrintWriter(sw);
				anexcepObj.printStackTrace(printWriter);
				printWriter.flush();
				sw.flush();
				return sw.toString();
			}
			else
				return null;
		}finally
		{
			
			try
			{
				if(sw != null)
					sw.close();
				if(printWriter != null)
					printWriter.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
	}
}
