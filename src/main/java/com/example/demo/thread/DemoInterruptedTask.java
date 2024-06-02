package com.example.demo.thread;

public class DemoInterruptedTask implements Runnable {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new DemoInterruptedTask());
        thread.start();

        // 主线程等待一段时间后中断任务线程
        Thread.sleep(5000);
        thread.interrupt();
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // 模拟执行一些任务
                System.out.println("Task is running...");
                Thread.sleep(1000); // 模拟耗时操作
            }
        } catch (InterruptedException e) {
            // 捕获到InterruptedException异常后重新设置线程的中断状态
            Thread.currentThread().interrupt();
            // 执行清理操作
            System.out.println("Cleaning up after interruption...");
        }
    }
}
