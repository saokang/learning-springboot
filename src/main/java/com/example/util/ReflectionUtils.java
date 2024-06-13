package com.example.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtils {

    /**
     * 获取对象的所有字段，包括私有字段
     *
     * @param obj 目标对象
     * @return 字段数组
     */
    public static Field[] getAllFields(Object obj) {
        Class<?> clazz = obj.getClass();
        return clazz.getDeclaredFields();
    }

    /**
     * 获取对象的特定字段的值，包括私有字段
     *
     * @param obj       目标对象
     * @param fieldName 字段名
     * @return 字段值
     */
    public static Object getFieldValue(Object obj, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    /**
     * 设置对象的特定字段的值，包括私有字段
     *
     * @param obj       目标对象
     * @param fieldName 字段名
     * @param value     字段值
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    /**
     * 获取对象的所有方法，包括私有方法
     *
     * @param obj 目标对象
     * @return 方法数组
     */
    public static Method[] getAllMethods(Object obj) {
        Class<?> clazz = obj.getClass();
        return clazz.getDeclaredMethods();
    }

    /**
     * 调用对象的特定方法，包括私有方法
     *
     * @param obj        目标对象
     * @param methodName 方法名
     * @param paramTypes 参数类型数组
     * @param args       参数值数组
     * @return 方法返回值
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object invokeMethod(Object obj, String methodName, Class<?>[] paramTypes, Object[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = obj.getClass().getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(obj, args);
    }

    /**
     * 创建类的新实例
     *
     * @param clazz 类
     * @param <T>   类的类型
     * @return 新实例
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <T> T createInstance(Class<T> clazz) throws InstantiationException, IllegalAccessException {
        return clazz.newInstance();
    }

    /**
     * 获取类的指定方法（包括私有方法）
     *
     * @param clazz      类
     * @param methodName 方法名
     * @param paramTypes 参数类型数组
     * @return 方法对象
     * @throws NoSuchMethodException
     */
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) throws NoSuchMethodException {
        Method method = clazz.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method;
    }

    /**
     * 获取类的指定字段（包括私有字段）
     *
     * @param clazz     类
     * @param fieldName 字段名
     * @return 字段对象
     * @throws NoSuchFieldException
     */
    public static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }
}
