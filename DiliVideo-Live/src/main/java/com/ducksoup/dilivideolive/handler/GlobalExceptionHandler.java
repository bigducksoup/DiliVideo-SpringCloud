package com.ducksoup.dilivideolive.handler;


import cn.dev33.satoken.exception.NotRoleException;
import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(NotRoleException.class)
    @ResponseBody
    public ResponseResult<Void> noRoles(NotRoleException e){
        return new ResponseResult<>(HttpStatus.HTTP_FORBIDDEN,"权限不足");
    }


}
