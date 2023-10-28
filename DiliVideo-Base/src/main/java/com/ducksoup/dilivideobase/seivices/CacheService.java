package com.ducksoup.dilivideobase.seivices;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public interface CacheService {

    <T>void cachePut(String key, T  value, long expireTime, TimeUnit timeUnit);

    <T>void cachePut(String cacheName,String key, T  value, long expireTime, TimeUnit timeUnit);

    <T>T cacheGet(String cacheName,String key);

    <T>T cacheGet(String key);

    void cacheRemove(String cacheName,String key);

    void cacheRemove(String key);

    <T>T cacheGetOrRefresh(String cacheName, String key, Supplier<T> supplier, long expireTime, TimeUnit timeUnit);

    <T>T cacheGetOrRefresh(String key, Supplier<T> supplier, long expireTime, TimeUnit timeUnit);


}
