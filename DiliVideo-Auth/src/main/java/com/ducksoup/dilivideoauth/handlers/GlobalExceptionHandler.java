package com.ducksoup.dilivideoauth.handlers;


import cn.dev33.satoken.exception.NotLoginException;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice(basePackages = "com.ducksoup.dilivideoauth.controller")
public class GlobalExceptionHandler {


    //sa-token抛出的未登录异常
    @ExceptionHandler(NotLoginException.class)
    @ResponseBody
    public ResponseResult<Object> handleNotLoginEx(NotLoginException e){
        return new ResponseResult<>(e.getCode(),e.getMessage());
    }
}
