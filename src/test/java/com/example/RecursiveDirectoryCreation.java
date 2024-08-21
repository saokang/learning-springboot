package com.example;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RecursiveDirectoryCreation {
    public static void main(String[] args) {
        String rootPath = "D:/Minana"; // 顶级目录
        createDirectories(rootPath, 3); // 递归深度为3
    }

    public static void createDirectories(String path, int depth) {
        if (depth == 0) {
            return;
        }

        Set<String> hashSet = new HashSet<>();
        while (hashSet.size() < 5) {
            hashSet.add(String.valueOf((char) ('a' + new Random().nextInt(26))));
        }
        ArrayList<String> arrayList = new ArrayList<>(hashSet);

        // 生成26个字母的数组
        String[] subDirs = new String[5];
        for (int i = 0; i < 5; i++) {
            subDirs[i] = arrayList.get(i);
        }

        for (String dir : subDirs) {
            File directory = new File(path, dir);
            if (!directory.exists()) {
                directory.mkdirs(); // 创建目录
                System.out.println("create " + directory.getAbsolutePath());
            }

            // 递归创建子目录
            createDirectories(directory.getPath(), depth - 1);
        }
    }
}
