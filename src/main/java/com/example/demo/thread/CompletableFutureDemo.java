package com.example.demo.thread;

import com.example.util.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureDemo {
    public static void main(String[] args) {
        List<CompletableFuture<Integer>> futureList = new ArrayList<>();

        // 提交异步任务
        for (int i = 0; i < 32; i++) {
            int taskId = i;
            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                LogUtils.debug("run task!");
                try {
                    Thread.sleep(taskId * 1000); // 模拟耗时操作
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LogUtils.debug("task end!");
                return taskId;
            });
            futureList.add(future);
        }

        // 等待所有异步任务完成并获取结果
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        futureList.forEach((futureDemo) -> {
            try {
                System.out.print(futureDemo.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            System.out.print("\t");
        });

    }
}
