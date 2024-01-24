package com.ducksoup.dilivideotranscoding.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
public class CallBack {

    private String url;

    private String method;

    private HandleResponse body;

    private Map<String, Object> form;

    private Map<String,String> headers;




}
