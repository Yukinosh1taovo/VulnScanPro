/*
package com.xueqiu.vulnscanpro.exception;

import com.xueqiu.vulnscanpro.model.entity.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 专门捕获账号密码错误的异常
    @ExceptionHandler(BadCredentialsException.class)
    public ApiResponse handleBadCredentialsException(BadCredentialsException e){
        log.error("登录失败，密码错误");
        return ApiResponse.error("用户名或密码错误");
    }


    // 捕获所有其他未预料的异常
    @ExceptionHandler(Exception.class)
    public ApiResponse handleException(Exception e){
        log.error("系统内部异常");
        return ApiResponse.error("系统繁忙,请稍后再试" + e.getMessage());
    }
}
*/
