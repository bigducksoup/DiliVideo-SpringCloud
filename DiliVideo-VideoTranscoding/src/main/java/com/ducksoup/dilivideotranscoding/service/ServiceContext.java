package com.ducksoup.dilivideotranscoding.service;

import java.util.concurrent.ConcurrentHashMap;

public class ServiceContext {

    public static ConcurrentHashMap<String,VideoHandleService> videoHandleServiceMap = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String,StorageService> storageServiceMap = new ConcurrentHashMap<>();
}
