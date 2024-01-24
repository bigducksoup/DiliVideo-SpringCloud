package com.ducksoup.dilivideoentity.result;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor

public class ResponseResult<T> implements Serializable {
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 提示信息，如果有错误时，前端可以获取该字段进行提示
     */
    private String msg;
    /**
     * 查询到的结果数据，
     */
    private T data;

    public ResponseResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseResult(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ResponseResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }



    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<T>(200, "success", data);
    }

    public static <T> ResponseResult<T> success() {
        return new ResponseResult<T>(200, "success");
    }



    public static <T> ResponseResult<T> error(String msg) {
        return new ResponseResult<T>(500, msg);
    }

    public static <T> ResponseResult<T> error(Integer code, String msg) {
        return new ResponseResult<T>(code, msg);
    }

    public static <T> ResponseResult<T> error(Integer code, String msg, T data) {
        return new ResponseResult<T>(code, msg, data);
    }


}