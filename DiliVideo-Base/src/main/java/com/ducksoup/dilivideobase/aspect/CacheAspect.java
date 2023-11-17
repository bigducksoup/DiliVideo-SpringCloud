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
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import javax.annotation.Resource;

@Aspect
public class CacheAspect  {

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


    @Around("RemovePointCut()")
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


    private final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

    @Around("getPointCut()")
    public Object aroundCache(ProceedingJoinPoint pjp){
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Cache annotation = signature.getMethod().getAnnotation(Cache.class);




        Object result = null;
        try {

            String key = parseKey(annotation.key(), pjp);

            Object cacheGet = cacheService.cacheGet(annotation.cacheName(), key);
            if (cacheGet == null){
                result = pjp.proceed();
                if (annotation.update()){
                    cacheService.cachePut(annotation.cacheName(),key,result,annotation.expireTime(),annotation.timeUnit());
                }
            }else {
                result = cacheGet;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return  result;

    }


    private final ExpressionParser parser = new SpelExpressionParser();
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();



    @Resource
    private ApplicationContext applicationContext;

    private String parseKey(String key,ProceedingJoinPoint joinPoint){
        if (key == null) {
            return key;
        }

        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setBeanResolver(new BeanFactoryResolver(applicationContext));
        String[] params = parameterNameDiscoverer.getParameterNames(((MethodSignature) joinPoint.getSignature()).getMethod());
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            context.setVariable(params[i], args[i]);
        }

        // Split the key by commas to support multiple expressions
        String[] expressions = key.split(",");
        StringBuilder result = new StringBuilder();
        for (String expression : expressions) {
            String value = parser.parseExpression(expression.trim()).getValue(context, String.class);
            result.append(value).append(",");
        }

        // Remove the trailing comma
        if (result.length() > 0) {
            result.setLength(result.length() - 1);
        }



        return result.toString();

    }






}
