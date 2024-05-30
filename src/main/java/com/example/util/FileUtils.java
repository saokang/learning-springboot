package com.example.util;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class FileUtils {

    // 判断文件是否存在
    public static boolean fileExists(String path) {
        return Files.exists(Paths.get(path));
    }

    // 获取文件大小，以适当的单位显示
    public static String getFileSize(String path) {
        long size = 0;
        try {
            size = Files.size(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (size < 1024) return size + " B";
        int exp = (int) (Math.log(size) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", size / Math.pow(1024, exp), pre);
    }

    // 创建文件
    public static boolean createFile(String path) {
        try {
            Files.createFile(Paths.get(path));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // 删除文件
    public static boolean deleteFile(String path) {
        try {
            return Files.deleteIfExists(Paths.get(path));
        } catch (IOException e) {
            return false;
        }
    }

    // 创建文件夹
    public static boolean createDirectory(String path) {
        try {
            Files.createDirectories(Paths.get(path));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // 删除文件夹
    public static boolean deleteDirectory(String path) {
        try {
            return Files.walk(Paths.get(path))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .allMatch(File::delete);
        } catch (IOException e) {
            return false;
        }
    }

    // 文件拷贝
    public static boolean copyFile(String source, String destination) {
        try {
            Files.copy(Paths.get(source), Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // 文件重命名
    public static boolean renameFile(String source, String newName) {
        try {
            return new File(source).renameTo(new File(new File(source).getParent(), newName));
        } catch (Exception e) {
            return false;
        }
    }

    // 获取文件夹下的文件列表
    public static List<String> listFilesInDirectory(String directory) {
        List<String> fileList = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(directory))) {
            for (Path path : stream) {
                fileList.add(path.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileList;
    }

    // 读取文本文件
    public static String readFileToString(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    // 写入文本文件
    public static void writeStringToFile(String filePath, String data) throws IOException {
        Files.write(Paths.get(filePath), data.getBytes());
    }

    // 按行读取文本文件
    public static List<String> readLines(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    // 按行写入文本文件
    public static void writeLines(String filePath, List<String> lines) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine(); // 添加新行
            }
        }
    }

    public static void printDirectoryTree(String startPath, int maxDepth) {
        printDirectoryTree(Path.of(startPath), 0, maxDepth, "");
    }

    private static void printDirectoryTree(Path path, int currentDepth, int maxDepth, String prefix)  {
        if (currentDepth > maxDepth || !Files.exists(path)) {
            return;
        }

        boolean isDirectory = Files.isDirectory(path);
        String entryPrefix = prefix + (currentDepth == maxDepth ? "└── " : "├── ");

        // 打印当前文件或目录名
        System.out.println(entryPrefix + path.getFileName() + (isDirectory ? "/" : ""));

        // 仅对目录执行递归遍历
        if (isDirectory && currentDepth < maxDepth) {
            try (var entries = Files.list(path)) {
                Iterator<Path> iterator = entries.iterator();
                while (iterator.hasNext()) {
                    Path next = iterator.next();
                    if (iterator.hasNext()) {
                        printDirectoryTree(next, currentDepth + 1, maxDepth, prefix + "│   ");
                    } else {
                        printDirectoryTree(next, currentDepth + 1, maxDepth, prefix + "    ");
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("========== fileExists Test ==========");
        String projectDir = System.getProperty("user.dir");
        System.out.println("当前工作目录是：" + projectDir);
        System.out.println("当前工作目录是否存在：" + fileExists(projectDir));

        String notExistedDir = projectDir + File.separator + "not_existed_dir";
        System.out.println("不存在的文件夹是：" + notExistedDir);
        System.out.println("不存在的文件夹是否存在：" + fileExists(notExistedDir));

        String existedFile = projectDir + File.separator + "pom.xml";
        System.out.println("当前存在的文件是：" + existedFile);
        System.out.println("当前存在的文件是否存在：" + fileExists(existedFile));

        String notExistedFile = projectDir + File.separator + "not_existed_file.txt";
        System.out.println("不存在的文件夹是：" + notExistedFile);
        System.out.println("不存在的文件夹是否存在：" + fileExists(notExistedFile));

        System.out.println("========== getFileSize Test ==========");
        System.out.println("当前工作文件夹大小：" + getFileSize(projectDir));
        System.out.println("当前工程的pom.xml文件大小：" + getFileSize(existedFile));

        System.out.println("========== copyFile Test ==========");
        // fixme 1 传入两个文件夹A B 代表把A拷贝到B目录下
        String ADir = projectDir + File.separator + "A";
        String BDir = projectDir + File.separator + "B";
        System.out.println("ADir 创建是否成功：" + createDirectory(ADir));
        System.out.println("BDir 创建是否成功：" + createDirectory(BDir));
        System.out.println("拷贝目录A到B目录下是否成功：" + copyFile(ADir, BDir));
        // 2 传入文件A.txt 文件夹B 代表将A.txt拷贝到B目录下
        // 3 传入两个文件A.txt B.txt 代表复制文件并重命名

        printDirectoryTree(projectDir, 10);
        System.out.println(deleteDirectory(ADir));
        System.out.println(deleteDirectory(BDir));

    }
}