package com.example.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyUtil {

    /**
     * 创建一个代理对象
     *
     * @param target 目标对象
     * @param <T>    目标对象类型
     * @return 代理对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(T target) {
        ClassLoader classLoader = target.getClass().getClassLoader();
        Class<?>[] interfaces = target.getClass().getInterfaces();
        return (T) Proxy.newProxyInstance(classLoader, interfaces, new DefaultInvocationHandler<>(target));
    }

    /**
     * 创建一个带有自定义处理器的代理对象
     *
     * @param target  目标对象
     * @param handler 自定义InvocationHandler
     * @param <T>     目标对象类型
     * @return 代理对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxyWithHandler(T target, InvocationHandler handler) {
        ClassLoader classLoader = target.getClass().getClassLoader();
        Class<?>[] interfaces = target.getClass().getInterfaces();
        return (T) Proxy.newProxyInstance(classLoader, interfaces, handler);
    }

    /**
     * 默认的InvocationHandler实现
     *
     * @param <T> 目标对象类型
     */
    private static class DefaultInvocationHandler<T> implements InvocationHandler {
        private final T target;

        public DefaultInvocationHandler(T target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 在方法调用之前执行的逻辑
            System.out.println("Before method: " + method.getName());

            // 调用目标对象的方法
            Object result = method.invoke(target, args);

            // 在方法调用之后执行的逻辑
            System.out.println("After method: " + method.getName());

            return result;
        }
    }
}
