package com.example.demo.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WebScraper {
    public static void main(String[] args) {
        String url = "https://learn.lianglianglee.com/"; // 替换为你想爬取的 URL
        String filePath = "D:/output.html"; // 指定保存的文件路径

        try {
            // 连接到网页并获取 HTML
            Document document = Jsoup.connect(url).get();

            // 打印网页标题
            String title = document.title();
            System.out.println("网页标题: " + title);

            // 打印网页内容
            System.out.println("网页 HTML:\n" + document.html());

            // 保存为 HTML 文件
            saveHtmlToFile(document.html(), filePath);
            System.out.println("HTML 已保存到: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveHtmlToFile(String html, String filePath) {
        try (FileWriter writer = new FileWriter(new File(filePath))) {
            writer.write(html);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
