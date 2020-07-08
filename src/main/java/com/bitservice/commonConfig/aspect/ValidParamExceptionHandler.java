package com.bitservice.commonConfig.aspect;

import com.bitservice.common.web.RestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@ControllerAdvice
public class ValidParamExceptionHandler {
    public final static Logger logger = LoggerFactory.getLogger(ValidParamExceptionHandler.class);

    @ExceptionHandler(value = {MethodArgumentNotValidException.class,BindException.class})
    @ResponseBody
    public RestResult allExceptionHandler(Exception e) {
        StringBuilder errMsg = new StringBuilder();
        StringBuilder dataMsg = new StringBuilder();
        if(e instanceof BindException){
            logger.info("请求参数异常！>>>{}",e.getMessage());
            BindException ex = (BindException) e;
            BindingResult bindingResult = ex.getBindingResult();

            errMsg.append("Invalid request:");
            for (int i = 0; i < bindingResult.getFieldErrors().size(); i++) {
                if (i > 0) {
                    errMsg.append(",");
                }
                FieldError error = bindingResult.getFieldErrors().get(i);
                errMsg.append(error.getField());
                errMsg.append(":");
                errMsg.append(error.getDefaultMessage());
            }
        }else if (e instanceof MethodArgumentNotValidException){
            MethodArgumentNotValidException validateException = (MethodArgumentNotValidException)e;
            BindingResult result = validateException.getBindingResult();
            final List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError error : fieldErrors) {
                String field = error.getField();
                dataMsg.append("【");
                dataMsg.append(field);
                dataMsg.append("】");

                errMsg.append(error.getDefaultMessage());
            }
        }
        return RestResult.error(dataMsg, "数据合法性校验失败！"+errMsg.toString());
    }
}