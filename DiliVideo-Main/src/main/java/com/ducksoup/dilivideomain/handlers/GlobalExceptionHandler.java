package com.ducksoup.dilivideomain.handlers;


import cn.dev33.satoken.exception.NotLoginException;
import cn.hutool.http.HttpStatus;
import com.ducksoup.dilivideoentity.result.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    //sa-token抛出的未登录异常
    @ExceptionHandler(NotLoginException.class)
    @ResponseBody
    public ResponseResult<Object> handleNotLoginEx(NotLoginException e){
        return new ResponseResult<>(e.getCode(),e.getMessage());
    }

    @ExceptionHandler(FileSizeLimitExceededException.class)
    @ResponseBody
    public ResponseResult<Object> handleFileSizeLimitExceededException(FileSizeLimitExceededException e){
        return new ResponseResult<>(HttpStatus.HTTP_FORBIDDEN,e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult<Object> handleException(Exception e){
        e.printStackTrace();
        log.error(e.getMessage());
        return new ResponseResult<>(HttpStatus.HTTP_INTERNAL_ERROR,e.getMessage());
    }


}
