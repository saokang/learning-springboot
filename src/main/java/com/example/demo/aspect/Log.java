package com.example.demo.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 可以注解在方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时保留注解信息
public @interface Log {

    String value() default "";
}
