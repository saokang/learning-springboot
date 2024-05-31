package com.example.util;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
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
        // 创建File对象
        File directory = new File(path);
        // 创建目录，包括所有必需的父目录
        return directory.mkdirs();
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


    /**
     * 复制文件或目录到目标路径。
     *
     * @param sourcePath 源文件或目录的路径
     * @param targetPath 目标路径
     * @throws IOException 如果在复制过程中发生IO错误
     */
    private static void copy(Path sourcePath, Path targetPath) {
        // 检查源路径是否存在
        if (Files.notExists(sourcePath)) {
            throw new IllegalArgumentException("Source path does not exist: " + sourcePath);
        }

        // 确定复制的目标路径
        if (Files.isDirectory(targetPath)) {
            targetPath = targetPath.resolve(sourcePath.getFileName());
        }

        // 复制文件或目录
        if (Files.isDirectory(sourcePath)) {
            // 递归复制目录
            Path finalTargetPath = targetPath;
            try {
                Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        Path targetDir = finalTargetPath.resolve(sourcePath.relativize(dir));
                        if (!Files.exists(targetDir)) {
                            Files.createDirectory(targetDir);
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.copy(file, finalTargetPath.resolve(sourcePath.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            // 直接复制文件
            try {
                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void copy(String src, String dest) {
        copy(Paths.get(src), Paths.get(dest));
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
    public static void writeStringToFile(String filePath, String data) {
        try {
            Files.write(Paths.get(filePath), data.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        printDirectoryTree(Paths.get(startPath), 0, maxDepth, "");
    }

    private static void printDirectoryTree(Path path, int currentDepth, int maxDepth, String prefix) {
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
        String testDir = projectDir + File.separator + "test_for_copy_file";
        deleteDirectory(testDir);
        String aDir = testDir + File.separator + "AAA";
        String bDir = testDir + File.separator + "BBB";
        String aTxt = testDir + File.separator + "aaa.txt";
        String bTxt = testDir + File.separator + "bbb.txt";
        String abcDir = testDir + File.separator + "AA/BB/CC";
        System.out.println(createDirectory(aDir));
        System.out.println(createDirectory(bDir));
        System.out.println(createFile(aTxt));
        writeStringToFile(aTxt, "testForAAATxt");
        copy(aDir, bDir);
        copy(aTxt, aDir);
        copy(aDir, bDir);
        copy(aDir, abcDir);
        copy(aTxt, bTxt);
        /*
            AAA
                aaa.txt
            BBB
                AAA
                    aaa.txt
            AA
                BB
                    CC
                        AAA
                            aaa.txt
            aaa.txt
            bbb.txt
         */
        printDirectoryTree(testDir, 10);


    }
}