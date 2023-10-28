package com.ducksoup.dilivideobase.aspect;


import com.ducksoup.dilivideobase.annotation.Cache;
import com.ducksoup.dilivideobase.annotation.CachePut;
import com.ducksoup.dilivideobase.annotation.CacheRemove;
import com.ducksoup.dilivideobase.seivices.CacheService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class CacheAspect {

    private CacheService cacheService;

    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Pointcut("@annotation(com.ducksoup.dilivideobase.annotation.CachePut)")
    public void putPointCut(){

    }

    @Pointcut("@annotation(com.ducksoup.dilivideobase.annotation.Cache)")
    public void getPointCut(){

    }

    @Pointcut("@annotation(com.ducksoup.dilivideobase.annotation.CacheRemove)")
    public void RemovePointCut(){

    }


    @Around("putPointCut()")
    public Object aroundPut(ProceedingJoinPoint pjp){

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        CachePut annotation = signature.getMethod().getAnnotation(CachePut.class);


        Object result = null;
        try {
            result = pjp.proceed();
            cacheService.cachePut(annotation.cacheName(),annotation.key(),result,annotation.expireTime(),annotation.timeUnit());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return result;
    }


    public Object aroundRemove(ProceedingJoinPoint pjp){

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        CacheRemove annotation = signature.getMethod().getAnnotation(CacheRemove.class);

        Object result = null;
        try {
            result = pjp.proceed();
            cacheService.cacheRemove(annotation.cacheName(),annotation.key());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return  result;

    }

    public Object aroundCache(ProceedingJoinPoint pjp){
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Cache annotation = signature.getMethod().getAnnotation(Cache.class);
        Object result = null;
        try {

            Object cacheGet = cacheService.cacheGet(annotation.cacheName(), annotation.cacheName());
            if (cacheGet == null){
                result = pjp.proceed();
                if (annotation.update()){
                    cacheService.cachePut(annotation.cacheName(),annotation.key(),result,annotation.expireTime(),annotation.timeUnit());
                }
            }else {
                result = cacheGet;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return  result;

    }




}
