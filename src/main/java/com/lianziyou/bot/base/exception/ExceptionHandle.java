package com.lianziyou.bot.base.exception;

import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.model.ErrorMessage;
import com.lianziyou.bot.service.sys.IErrorMessageService;
import com.lianziyou.bot.utils.sys.JwtUtil;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

/**
 * @author Administrator
 */
@ControllerAdvice
@Log4j2
public class ExceptionHandle {

    @Resource
    IErrorMessageService errorMessageService;

    /**
     * 异常处理
     *
     * @param e 异常信息
     * @return 返回类是我自定义的接口返回类，参数是返回码和返回结果，异常的返回结果为空字符串
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ApiResult<Void> handle(Exception e) {
        getMessage(e.getMessage(), e.getStackTrace());
        //自定义异常返回对应编码
        if (e instanceof BussinessException) {
            BussinessException ex = (BussinessException) e;
            return ApiResult.finalBuild(ex.getMessage());
        }
        //其他异常报对应的信息
        else {
            log.error("[系统异常]{}", e.getMessage(), e);
            return ApiResult.finalBuild("系统异常");
        }
    }

    @ExceptionHandler(value = AsyncRequestTimeoutException.class)
    public void handleAsyncRequestTimeoutException(AsyncRequestTimeoutException e) {
        getMessage(e.getMessage(), e.getStackTrace());
        log.error("[AsyncRequestTimeoutException]{}", e.getMessage());

    }

    private void getMessage(String message, StackTraceElement[] stackTrace) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setErrorMessage(message);
        errorMessage.setUrl(request.getServletPath());
        for (StackTraceElement stackTraceElement : stackTrace) {
            if (stackTraceElement.toString().contains("com.lianziyou.bot")) {
                errorMessage.setPosition(stackTraceElement.toString());
            }
        }
        errorMessage.setErrorMessage(null == errorMessage.getErrorMessage() ? stackTrace[0].toString() : errorMessage.getErrorMessage());
        errorMessage.setUserId(JwtUtil.getUserId());
        errorMessageService.save(errorMessage);
    }
}
