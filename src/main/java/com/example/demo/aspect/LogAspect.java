package com.example.demo.aspect;

import com.example.util.LogUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * 2024-06-06 00:45:27 687 INFO --- [main] Executing method: getLog
 * 2024-06-06 00:45:27 688 INFO --- [main] LogExecution value: call getLog method
 * 2024-06-06 00:45:27 688 INFO --- [main] Arguments: [375336]
 * id: 375336
 * 2024-06-06 00:45:27 688 INFO --- [main] Method getLog returned with value: 375336
 * 2024-06-06 00:45:27 690 INFO --- [main] Method String com.example.demo.aspect.LogServiceImplDemo.getLog(Integer) executed in 10 ms
 * 2024-06-06 00:45:27 690 INFO --- [main] Executing method: getLogInfo
 * 2024-06-06 00:45:27 690 INFO --- [main] LogExecution value: call getLogInfo method
 * 2024-06-06 00:45:27 690 INFO --- [main] Arguments: [hwx1302778, huangzhikang]
 * id: hwx1302778 logName: huangzhikang
 * 2024-06-06 00:45:27 690 INFO --- [main] Method getLogInfo returned with value: {logName=huangzhikang, id=hwx1302778}
 * 2024-06-06 00:45:27 690 INFO --- [main] Method Map com.example.demo.aspect.LogServiceImplDemo.getLogInfo(String,String) executed in 0 ms
 * 2024-06-06 00:45:27 690 INFO --- [main] Executing method: getLogWithException
 * 2024-06-06 00:45:27 690 INFO --- [main] LogExecution value:
 * 2024-06-06 00:45:27 690 INFO --- [main] Arguments: empty args
 * 2024-06-06 00:45:27 691 ERROR --- [main] Method getLogWithException thrown exception: one more failed thing...
 * 2024-06-06 00:45:27 691 ERROR --- [main] Method boolean com.example.demo.aspect.LogServiceImplDemo.getLogWithException() failed in 1 ms
 */
@Aspect
@Configuration
public class LogAspect {

    @Pointcut("@annotation(Log)")
    public void logPointcut() {
        // Pointcut for methods annotated with @Log
    }

    @Before("logPointcut()")
    public void beforeMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Log logExecution = signature.getMethod().getAnnotation(Log.class);
        LogUtils.info("Executing method: {}", signature.getMethod().getName());
        LogUtils.info("LogExecution value: {}", logExecution.value());
        LogUtils.info("Arguments: {}", joinPoint.getArgs().length == 0 ? "empty args" : Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "logPointcut()", returning = "result")
    public void afterMethodReturning(JoinPoint joinPoint, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LogUtils.info("Method {} returned with value: {}", signature.getMethod().getName(), result);
    }

    @AfterThrowing(pointcut = "logPointcut()", throwing = "exception")
    public void afterMethodThrowing(JoinPoint joinPoint, Throwable exception) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LogUtils.error("Method {} thrown exception: {}", signature.getMethod().getName(), exception.getMessage());
    }

    @Around("logPointcut()")
    public Object aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long timeTaken = System.currentTimeMillis() - startTime;
            LogUtils.info("Method {} executed in {} ms", joinPoint.getSignature(), timeTaken);
            return result;
        } catch (Throwable ex) {
            long timeTaken = System.currentTimeMillis() - startTime;
            LogUtils.error("Method {} failed in {} ms", joinPoint.getSignature(), timeTaken);
            throw ex;
        }
    }

}
