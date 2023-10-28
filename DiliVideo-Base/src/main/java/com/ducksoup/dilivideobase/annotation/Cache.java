package com.ducksoup.dilivideobase.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {

    String cacheName() default "Cache";

    String key();

    long expireTime() default 10;

    TimeUnit timeUnit() default TimeUnit.MINUTES;

    boolean update() default true;

}
