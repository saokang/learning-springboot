package com.example.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class ThreadUtil {

    private static final int corePoolSize = Runtime.getRuntime().availableProcessors();
    private static final int maxPoolSize = corePoolSize;
    private static final long aliveTime = 60;
    private static final TimeUnit unit = TimeUnit.SECONDS;
    private static final int defaultWorkQueueCapacity = 1 << 10;  // 1 << 10 => 1 * 2^10


    private static ExecutorService commonExecutor;

    static {
        initCommonThreadPool();
    }

    public static void initCommonThreadPool() {
        commonExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, aliveTime, unit,
                new ArrayBlockingQueue<>(defaultWorkQueueCapacity),
                newDefaultNamePrefixThreadFactory(),
                newDefaultRejectedExecutorHandler());
    }

    // 提交任务
    public static void runAsync(Runnable runnable) {
        commonExecutor.execute(runnable);
    }

    public static <T> Future<T> runAsyncAndReturn(Callable<T> callable) {
        return commonExecutor.submit(callable);
    }

    // 关闭线程池
    public static void shutdownCommonThreadPool() {
        if (null != commonExecutor) {
            commonExecutor.shutdown();
        }
    }
    // 自定义线程池

    /**
     * 执行具有相同逻辑但不同参数的多个任务
     *
     * @param taskLogic  任务的核心逻辑，接受一个参数
     * @param parameters 参数列表，每个参数用于执行一次任务逻辑
     * @param <T>        参数类型
     */
    public static <T> void executeTasksWithParams(Consumer<T> taskLogic, List<T> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return;
        }

        final CountDownLatch latch = new CountDownLatch(parameters.size());
        for (T param : parameters) {
            commonExecutor.submit(() -> {
                try {
                    taskLogic.accept(param);
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();  // 等待所有任务执行完成
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // ================================================== ThreadFactory and RejectedExecutionHandler
    // 两者都是接口，可以通过 实现 和 new 的方法自定义
    private static ThreadFactory newDefaultNamePrefixThreadFactory() {
        return newNamePrefixThreadFactory("common-thread");
    }

    // 自定义线程工厂
    public static ThreadFactory newNamePrefixThreadFactory(String namePrefix) {
        return new ThreadFactory() {

            private AtomicInteger count = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, namePrefix + "-" + count.incrementAndGet());
            }
        };
    }

    private static RejectedExecutionHandler newDefaultRejectedExecutorHandler() {
        return new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                // 处理拒绝的任务，包括日志记录，任务重试，数据记录，等待系统恢复后重新提交任务（重试机制）
                System.err.println("Task rejected: " + r.toString());
            }
        };
    }

    /**
     * 私有静态内部类（仅测试使用）
     * 测试：initCommonThreadPool() 使用 new SynchronousQueue<>(true)
     */
    private static class CustomRejectedExecutorHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            // 处理拒绝的任务，包括日志记录，任务重试，数据记录，等待系统恢复后重新提交任务（重试机制）
            System.err.println("Task rejected: " + r.toString());
            retryExecution(r, executor);
        }

        public void retryExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                System.out.println("Task reSubmit: " + r.toString());
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                System.err.println("Task reSubmit failed: " + r.toString());
                /*
                 * Thread.currentThread().interrupt() 方法用于中断当前线程。
                 * 当一个线程调用interrupt()方法时，它会设置线程的中断状态为true。
                 * 但是，如果线程正在等待、阻塞或者睡眠时调用interrupt()方法，它会抛出InterruptedException异常，并且清除线程的中断状态。
                 * 在这种情况下，可以通过Thread.currentThread().interrupt()重新设置线程的中断状态。
                 * 通常情况下，如果你的线程被中断了，你可以在捕获到InterruptedException异常后调用Thread.currentThread().interrupt()
                 * 来重新设置线程的中断状态，以便在后续的代码中检查中断状态并采取相应的处理措施，例如终止线程、清理资源等。
                 */
                // Thread.currentThread().interrupt();
            }
        }
    }

    // ================================================== single thread methods
    public static Thread newThread(Runnable runnable, String name) {
        return new Thread(runnable, name);
    }

    public static Thread newThreadAndStart(Runnable runnable, String name) {
        Thread thread = new Thread(runnable, name);
        thread.start();
        return thread;
    }

    public static String getCurrentThreadName() {
        return Thread.currentThread().getName();
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sleepSeconds(int seconds) {
        sleep(seconds * 1000L);
    }

    public static void main(String[] args) {
        System.out.println(Runtime.getRuntime().availableProcessors());

        Thread thread = newThread(() -> {
            System.out.println(getCurrentThreadName() + ": Task is Demo!");
        }, "newThread-01");
        thread.start();
        System.out.println("Main Thread: demo!");

        Thread newThreadAndStart = newThreadAndStart(() -> {
            System.out.println(getCurrentThreadName() + " Task is Experiment");
        }, "newThreadAndStart-01");
        // sleepSeconds(2);
        System.out.println("Main Thread: newThreadAndStart!");


        runAsync(() -> System.out.println(getCurrentThreadName() + ": thread pool runAsync task!"));

        Future<String> future = runAsyncAndReturn(() -> {
            System.out.println(getCurrentThreadName() + ": thread pool runAsyncAndReturn task!");
            sleep(500);
            return "runAsyncAndReturn Demo Result";
        });

        try {
            System.out.println("Main Thread: get block Future result => " + future.get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        // 演示执行一组Future并获取结果
        List<Future<Integer>> futureList = new ArrayList<>();

        for (int i = 0; i < 32; i++) {
            int taskId = i;
            Future<Integer> futureResult = runAsyncAndReturn(() -> {
                sleepSeconds(taskId);
                return taskId;
            });
            futureList.add(futureResult);
        }

        futureList.forEach(futureDemo -> {
            try {
                System.out.print(futureDemo.get());
                System.out.print("\t");
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println();

        executeTasksWithParams((param) -> {
            // 简单处理逻辑
            System.out.println("Get Iphone is: " + param);
        }, Arrays.asList("Apple","Xiaomi","ViVO", "OPPO", "OnePlus"));

        shutdownCommonThreadPool();
    }
}
