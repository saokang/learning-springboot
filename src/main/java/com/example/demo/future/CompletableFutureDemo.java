package com.example.demo.future;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 请教一个问题：
 * 假设一个方法 queryUserBatch 模拟查询方法，每次查询最多 50 个 ID，耗时 100ms
 * 另一个方法：queryUsers 传入100w条用户id数据 2w个线程 每个线程异步处理50条数据，理论上 queryUserBatch 每次耗时都在100ms左右
 * 但实际上 2w个任务 有1000条任务耗时 > 400ms, 剩下的任务耗时在120ms以下 想问一下什么原因？
 *
 * 如果传入1000条数据 20个线程 每个queryUserBatch 耗时都< 150ms
 * 如果传入10000条数据 200个线程 每个queryUserBatch 耗时都在 200ms左右
 * 如果传入100000条数据 2000个线程 30%的queryUserBatch 耗时在 400ms左右
 * 如果传入1000000条数据 20000个线程 10%的queryUserBatch 耗时在 400ms左右
 */

public class CompletableFutureDemo {

    // 模拟查询方法，每次查询最多 50 个 ID，耗时 100ms
    public Map<Long, String> queryUserBatch(List<Long> userIds) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        Thread.sleep(100); // 模拟耗时
        // 模拟返回用户ID和姓名
        Map<Long, String> collect = userIds.stream().collect(Collectors.toMap(id -> id, id -> "User" + id));
        System.out.println(Thread.currentThread().getName() + ":耗时: " + (System.currentTimeMillis() - startTime));
        return collect;
    }

    public Map<Long, String> queryUsers(List<Long> userIds, ExecutorService executor) throws InterruptedException, ExecutionException {
        int totalUsers = userIds.size();

        // 将 1000 个用户 ID 切分成多个批次，每批次最多 50 个
        List<List<Long>> batches = new ArrayList<>();
        for (int i = 0; i < totalUsers; i += 50) {
            batches.add(userIds.subList(i, Math.min(totalUsers, i + 50)));
        }

        // 使用 CompletableFuture 并行查询每个批次
        List<CompletableFuture<Map<Long, String>>> futures = batches.stream()
                .map(batch -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return queryUserBatch(batch);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }, executor))
                .collect(Collectors.toList());

        // 等待所有批次查询完成并合并结果
        Map<Long, String> result = new ConcurrentHashMap<>();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenAccept(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .forEach(result::putAll)
                ).join();

        return result;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        CompletableFutureDemo userService = new CompletableFutureDemo();

        // 实际生产中需要自己实现，避免使用jdk自带四种线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(20_0000);

        List<Long> userIds = new ArrayList<>();
        for (long i = 1; i <= 1_000_0000; i++) {
            userIds.add(i);
        }

        long startTime = System.currentTimeMillis();
        Map<Long, String> users = userService.queryUsers(userIds, threadPool);
        long endTime = System.currentTimeMillis();

        System.out.println("查询耗时: " + (endTime - startTime) + " ms");
        // users.forEach((k,v) -> System.out.println(k + ": " + v));
        threadPool.shutdown();
    }
}
