package com.example.util;

public class TimeRecorder {

    public static final String TIME_Millis = "milliseconds";
    public static final String TIME_Nano = "nanoseconds";
    private long startTime;
    private long endTime;
    private boolean running;

    // 开始计时
    public void start() {
        this.startTime = System.nanoTime();
        this.running = true;
    }

    // 停止计时
    public void stop() {
        this.endTime = System.nanoTime();
        this.running = false;
    }

    // 获取已经过的时间（纳秒）
    public long getElapsedTimeNanos() {
        if (running) {
            return System.nanoTime() - startTime;
        } else {
            return endTime - startTime;
        }
    }

    // 获取已经过的时间（毫秒）
    public long getElapsedTimeMillis() {
        return getElapsedTimeNanos() / 1_000_000;
    }

    // 获取已经过的时间（秒）
    public double getElapsedTimeSeconds() {
        return getElapsedTimeMillis() / 1000.0;
    }

    // 重置计时器
    public void reset() {
        startTime = 0;
        endTime = 0;
        running = false;
    }

    public TimeRecorder andStart() {
        start();
        return this;
    }

    public void stopAndPrint(String tag, String description, String unit) {
        stop();
        if (TIME_Millis.equals(unit)) {
            System.out.println("[ " + tag + " ] " + description + getElapsedTimeMillis() + " milliseconds");
        } else if (TIME_Nano.equals(unit)) {
            System.out.println("[ " + tag + " ] " + description + getElapsedTimeNanos() + " nanoseconds");
        } else {
            System.out.println("[ " + tag + " ] " + description + getElapsedTimeSeconds() + " seconds");
        }
    }

    public void stopAndPrint(String tag) {
        stopAndPrint(tag, "Task completed in ", null);
    }

    public void stopAndPrint(String tag, String description) {
        stopAndPrint(tag, description, null);
    }

    public static void main(String[] args) {
        TimeRecorder timer = new TimeRecorder();
        // 开始计时
        timer.start();
        // 执行一些需要计时的代码
        performTask();
        // 停止计时
        timer.stop();
        // 输出执行时间
        System.out.println("Task completed in " + timer.getElapsedTimeMillis() + " milliseconds.");
        System.out.println("Task completed in " + timer.getElapsedTimeSeconds() + " seconds.");

        TimeRecorder timeRecorder = new TimeRecorder().andStart();
        performTask();
        timeRecorder.stopAndPrint(Thread.currentThread().getName());
    }

    private static void performTask() {
        // 模拟一些处理逻辑
        try {
            Thread.sleep(2300); // 模拟延时
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}