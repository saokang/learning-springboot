package com.example.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtils {
    private static final String LOG_FILE = "app.log"; // 日志文件的默认路径
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");

    // 枚举定义日志级别
    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }

    // 控制是否输出到文件
    private static boolean logToFile = false;

    // 设置是否日志输出到文件
    public static void setLogToFile(boolean toFile) {
        logToFile = toFile;
    }

    // 通用日志方法
    private static void log(LogLevel level, String message, Object... params) {
        String formattedMessage = formatMessage(level, message, params);
        if (logToFile) {
            writeToFile(formattedMessage);
        }
        System.out.println(formattedMessage);
    }

    // 格式化消息
    private static String formatMessage(LogLevel level, String message, Object... params) {
        String timestamp = dateFormat.format(new Date());
        String formattedMessage = formatWithBraces(message, params);
        return String.format("%s %s --- [%s] %s", timestamp, level, Thread.currentThread().getName(), formattedMessage);
    }

    // 写入到文件
    private static void writeToFile(String message) {
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            out.println(message);
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

    /**
     * 使用 `{}` 作为占位符进行字符串格式化。
     *
     * @param message 带有 `{}` 占位符的字符串
     * @param params  替换占位符的参数
     * @return 格式化后的字符串
     */
    public static String formatWithBraces(String message, Object... params) {
        // 早期返回，如果没有参数或消息为空
        if (params == null || params.length == 0 || message == null) {
            return message;
        }

        // 用于构建最终的字符串结果
        StringBuilder builder = new StringBuilder(message.length() + 64);
        int paramIndex = 0;
        int start = 0;
        int bracesIndex;

        // 循环处理字符串中的占位符
        while ((bracesIndex = message.indexOf("{}", start)) != -1) {
            // 将前面的部分和替换后的参数拼接
            builder.append(message, start, bracesIndex);
            if (paramIndex < params.length) {
                builder.append(params[paramIndex++]);
            } else {
                builder.append("{}"); // 参数不足时回退到原样
            }
            start = bracesIndex + 2;
        }

        // 添加最后一个占位符后面的所有字符
        builder.append(message.substring(start));

        return builder.toString();
    }

    // 具体级别的日志方法
    public static void debug(String message, Object... params) {
        log(LogLevel.DEBUG, message, params);
    }

    public static void info(String message, Object... params) {
        log(LogLevel.INFO, message, params);
    }

    public static void warn(String message, Object... params) {
        log(LogLevel.WARN, message, params);
    }

    public static void error(String message, Object... params) {
        log(LogLevel.ERROR, message, params);
    }

    public static void main(String[] args) {
        // 设置是否将日志写入文件
        LogUtils.setLogToFile(true);

        LogUtils.debug("Debugging application... User: {}, Time: {}", "Alice", System.currentTimeMillis());
        LogUtils.info("Application is running");
        LogUtils.warn("Low memory warning");
        LogUtils.error("Failed to load resource: {}", "resource_name");

        // 设置日志输出到控制台
        LogUtils.setLogToFile(false);

        LogUtils.info("Now logging to the console");
    }
}