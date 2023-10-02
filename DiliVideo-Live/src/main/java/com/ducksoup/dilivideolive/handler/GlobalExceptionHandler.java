package com.ducksoup.dilivideolive.handler;


import cn.dev33.satoken.exception.NotRoleException;
import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(NotRoleException.class)
    @ResponseBody
    public ResponseResult<Void> noRoles(NotRoleException e){
        return new ResponseResult<>(HttpStatus.HTTP_FORBIDDEN,"权限不足");
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult<Object> handleException(Exception e){
        e.printStackTrace();
        log.error(e.getMessage());
        return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,e.getMessage());
    }


}
