package com.ducksoup.dilivideobase.seivices.Impl;


import com.ducksoup.dilivideobase.seivices.CacheService;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class RedisCacheService implements CacheService {


    private final RedisTemplate<Serializable,Object> redisTemplate;

    public RedisCacheService(RedisTemplate<Serializable,Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }


    @Override
    public <T> void cachePut(String key, T value, long expireTime, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key,value,expireTime,timeUnit);
    }

    @Override
    public <T> void cachePut(String cacheName, String key, T value, long expireTime, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(cacheName+":"+key,value,expireTime,timeUnit);
    }

    @Override
    public <T> T cacheGet(String cacheName, String key) {
        return (T)redisTemplate.opsForValue().get(cacheName+":"+key);
    }

    @Override
    public <T> T cacheGet(String key) {
        return (T)redisTemplate.opsForValue().get(key);
    }

    @Override
    public void cacheRemove(String cacheName, String key) {
        redisTemplate.delete(cacheName+":"+key);
    }

    @Override
    public void cacheRemove(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public <T> T cacheGetOrRefresh(String cacheName, String key, Supplier<T> supplier, long expireTime, TimeUnit timeUnit) {

        Object val = redisTemplate.opsForValue().get(cacheName + ":" + key);
        if (val!=null){
            return (T)val;
        }
        T value = supplier.get();
        redisTemplate.opsForValue().set(cacheName+":"+key,value,expireTime,timeUnit);
        return value;

    }

    @Override
    public <T> T cacheGetOrRefresh(String key, Supplier<T> supplier, long expireTime, TimeUnit timeUnit) {
        Object val = redisTemplate.opsForValue().get(key);
        if (val!=null){
            return (T)val;
        }
        T value = supplier.get();
        redisTemplate.opsForValue().set(key,value,expireTime,timeUnit);
        return value;
    }
}
