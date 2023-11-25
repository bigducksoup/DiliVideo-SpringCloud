package com.ducksoup.dilivideotext.aop.annonation;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PerformanceLog {
}
