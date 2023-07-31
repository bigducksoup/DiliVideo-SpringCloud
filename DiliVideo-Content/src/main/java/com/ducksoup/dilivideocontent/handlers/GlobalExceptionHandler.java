package com.ducksoup.dilivideocontent.handlers;


import cn.dev33.satoken.exception.NotLoginException;
import com.ducksoup.dilivideoentity.Result.ResponseResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
public class GlobalExceptionHandler {


    //sa-token抛出的未登录异常
    @ExceptionHandler(NotLoginException.class)
    @ResponseBody
    public ResponseResult<Object> handleNotLoginEx(NotLoginException e){
        return new ResponseResult<>(e.getCode(),e.getMessage());
    }
}
