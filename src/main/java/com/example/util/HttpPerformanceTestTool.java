package com.example.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class HttpPerformanceTestTool {

    private final int threadCount;
    private final ExecutorService executorService;
    private final CountDownLatch latch;

    public HttpPerformanceTestTool(int threadCount) {
        this.threadCount = threadCount;
        this.executorService = Executors.newFixedThreadPool(threadCount);
        this.latch = new CountDownLatch(threadCount);
    }

    public void runTests(String targetUrl, String method, String jsonData, Consumer<String> responseHandler) throws InterruptedException {
        List<Future<Long>> futures = new ArrayList<>();
        List<Long> responseTimes = new ArrayList<>(); // 存储每次请求的响应时间

        // 提交任务
        for (int i = 0; i < threadCount; i++) {
            futures.add(executorService.submit(() -> {
                latch.await(); // 等待所有线程准备好
                long startTime = System.nanoTime();
                String response;

                try {
                    if ("POST".equalsIgnoreCase(method)) {
                        response = sendPostRequest(targetUrl, jsonData);
                    } else {
                        response = sendGetRequest(targetUrl);
                    }
                    responseHandler.accept(response); // 处理响应
                } catch (Exception e) {
                    e.printStackTrace();
                }

                long elapsedTime = System.nanoTime() - startTime;
                synchronized (responseTimes) {
                    responseTimes.add(elapsedTime / 1_000_000); // 将纳秒转换为毫秒
                }
                return elapsedTime;
            }));
            latch.countDown(); // 准备好一个线程
        }

        // 让所有线程同时开始
        latch.await();

        // 收集结果
        long totalTimeMs = 0;
        for (Future<Long> future : futures) {
            try {
                totalTimeMs += future.get() / 1_000_000; // 转换为毫秒
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        // 输出每次请求的响应时间
        System.out.println("\n每次请求的响应时间 (毫秒):");
        System.out.printf("%-10s %s%n", "请求号", "响应时间(ms)");
        for (int i = 0; i < responseTimes.size(); i++) {
            System.out.printf("%-10d %d%n", i + 1, responseTimes.get(i));
        }

        // 计算总时间和平均时间
        long averageTimeMs = totalTimeMs / threadCount;
        double totalTimeSeconds = totalTimeMs / 1000.0;
        double averageTimeSeconds = averageTimeMs / 1000.0;

        // 输出结果
        // System.out.println("\n总时间: " + totalTimeMs + " 毫秒 (" + totalTimeSeconds + " 秒)");
        System.out.println("\n平均时间: " + averageTimeMs + " 毫秒 (" + averageTimeSeconds + " 秒)");
    }

    private String sendGetRequest(String targetUrl) throws Exception {
        StringBuilder response = new StringBuilder();
        URL url = new URL(targetUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000); // 设置连接超时
        connection.setReadTimeout(5000);    // 设置读取超时

        // 读取响应
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
        return response.toString();
    }

    private String sendPostRequest(String targetUrl, String jsonData) throws Exception {
        StringBuilder response = new StringBuilder();
        URL url = new URL(targetUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(5000); // 设置连接超时
        connection.setReadTimeout(5000);    // 设置读取超时
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        // 发送请求体
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonData.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // 读取响应
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
        return response.toString();
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        HttpPerformanceTestTool tool = new HttpPerformanceTestTool(100); // 设置并发线程数为 100
        String targetUrl = "https://httpbin.org/post"; // 需要测试的 URL
        String jsonData = "{\"key\":\"value\"}"; // 发送的 JSON 数据

        // 测试 POST 请求
        System.out.println("开始 POST 请求的压测...");
        tool.runTests(targetUrl, "POST", jsonData, response -> {
            // 处理响应
            // System.out.println("POST 响应: " + response);
        });

        // 测试 GET 请求
        targetUrl = "https://httpbin.org/get";
        System.out.println("开始 GET 请求的压测...");
        tool.runTests(targetUrl, "GET", null, response -> {
            // 处理响应
            // System.out.println("GET 响应: " + response);
        });

        tool.shutdown();
    }
}
