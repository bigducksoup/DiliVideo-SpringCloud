package com.ducksoup.dilivideolive.entity;



import lombok.Data;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

@Data
public class Request {


    private Map<String,String> headers;

    private Map<String,Object> params;

    private HttpMethod method;

    private Object data;

    private String url;

    public Request(){
        headers = new HashMap<>();
        params = new HashMap<>();

    }

    public Request(String url){
        headers = new HashMap<>();
        params = new HashMap<>();
        this.url = url;
    }


    public Request(String url,HttpMethod method){
        headers = new HashMap<>();
        params = new HashMap<>();
        this.url = url;
        this.method = method;
    }


    public static Request init(){
        return new Request();
    }


    public static Request init(String url,HttpMethod method){
        return new Request(url,method);
    }

    public Request addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public Request addHeaders(Map<String,String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public Request addParam(String key, Object value) {
        params.put(key, value);
        return this;
    }

    public Request addData(Object value) {
        data = value;
        return this;
    }


}
