package com.ducksoup.dilivideoauth.aop.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class PerformanceLogAspect {

    @Pointcut("@annotation(com.ducksoup.dilivideoauth.aop.annonation.PerformanceLog)")
    public void pointcut(){

    }

    @Around(value = "pointcut()")
    public Object around(ProceedingJoinPoint pjp){

        //get method name
        String methodName = pjp.getSignature().getName();

        //get package path
        String packageName = pjp.getSignature().getDeclaringTypeName();

        long start = System.currentTimeMillis();
        Object result = null;
        try {
            result = pjp.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        long end = System.currentTimeMillis();
        log.info(packageName+"::::"+methodName+"耗时:" + (end - start) + "ms");
        return result;
    }



}
