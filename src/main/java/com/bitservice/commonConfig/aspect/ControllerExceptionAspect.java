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
import java.io.PrintWriter;
import java.io.StringWriter;
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

	private RestResult handlerException(ProceedingJoinPoint pjp, Throwable e) {
		log.error(">>>>出现异常:{}",getExceptionStackTrace(e), e);
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
		msg = getRuleMsg(msg);
		return RestResult.error(null, msg);
	}


	/**
	 *
	 * 1)如果报异常含uuid+登记系统的联系张凯，uuid+税务系统的联系赵友嘉，其他含uuid的联系李祥
	 * 2)连接超时的连接王富强
	 * 3)服务器异常DB——error的联系张永胜
	 * 4)参数异常 联系 王小天
	 **/
	private String getRuleMsg(String msg){
		if(msg == null){
			return null;
		}
		//region 1)如果报异常含uuid+登记系统的联系张凯，uuid+税务系统的联系赵友嘉，其他含uuid的联系李祥
		if(msg.indexOf("uuid")!=-1||msg.indexOf("UUID")!=-1){
			if(msg.indexOf("登记")!=-1){
				return msg+"  ZK";
			}
			if(msg.indexOf("税务")!=-1){
				return msg+"  ZYJ";
			}
			return msg + "  LX";
		}
		//endregion

		//region 2)连接超时的连接王富强
		if(msg.indexOf("connect")!=-1 || msg.indexOf("CONNECT")!=-1){
			return msg + "  WFQ";
		}
		//endregion

		//region 3)服务器异常DB——error的联系张永胜
		if(msg.indexOf("DB_ERROR")!=-1 || msg.indexOf("db_error")!=-1){
			return msg + "  ZYS";
		}
		//endregion

		//region 3)参数异常 联系 王小天
		if(msg.indexOf("参数有误")!=-1){
			return msg + "  WXT";
		}
		//endregion
		return msg;
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
