package com.example.controller;

import com.example.util.LogUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/files")
public class FileController implements BeanPostProcessor {


    @Autowired
    private HttpServletRequest request;

    private String getBaseUrl(String url) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        String baseUrl = scheme + "://" + serverName + ":" + serverPort + contextPath;
        if (null == url) {
            return baseUrl;
        }
        if (!url.startsWith("/")) {
            url = "/" + url;
        }
        return baseUrl + url;
    }

    private String getBaseUrl() {
        return getBaseUrl(null);
    }

    private final String uploadFolder = System.getProperty("user.dir") + File.separator + "upload";
    private final Path uploadPath = Path.of(uploadFolder);

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // File dir = new File(uploadFolder);
        // LogUtils.debug("beanName: {}", beanName);
        // LogUtils.debug("os: {}, File.separator: {}", System.getProperty("os.name"), File.separator);
        // LogUtils.debug("upload folder: {}", dir);
        // if (!dir.exists()) {
        //     LogUtils.debug("upload folder is not existed!");
        //     dir.mkdirs();
        //     LogUtils.debug("upload folder is initialize!");
        // }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }


    // 上传文件
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        File dir = new File(uploadFolder);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            Files.copy(file.getInputStream(), uploadPath.resolve(file.getOriginalFilename()));
            model.addAttribute("message", "You successfully uploaded " + file.getOriginalFilename() + "!");
        } catch (Exception e) {
            model.addAttribute("message", "Failed to upload " + file.getOriginalFilename() + "!");
        }
        return "redirect:/files";
    }

    // 文件列表
    @GetMapping()
    public String listUploadFiles(Model model) throws IOException {
        List<String> serveFiles = Files.walk(uploadPath, 1)
                .filter(path -> !path.equals(uploadPath) && !Files.isDirectory(path))
                .map(uploadPath::relativize)
                .map(path -> {
                    // LogUtils.debug("===> map path: {}, isFolder: {}", path.toString(), Files.isDirectory(path));
                    return MvcUriComponentsBuilder
                            .fromMethodName(FileController.class, "serveFile", path.toString())
                            .build().toUri().toString();
                })
                .collect(Collectors.toList());
        // serveFiles.forEach(file -> LogUtils.debug("file: {}", file));
        model.addAttribute("files", serveFiles);
        return "uploadForm";
    }

    // 下载文件
    @GetMapping("/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        // LogUtils.debug("serveFile: {}", filename);
        try {
            Path file = uploadPath.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8) + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/upload/v1")
    @ResponseBody
    public ResponseEntity<HashMap<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();

        String uuid = UUID.randomUUID().toString();
        // aaa.png
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = uuid + suffix;
        String uploadTimeFolder = uploadFolder + File.separator + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        // LogUtils.debug("uploadTimeFolder: {}", uploadTimeFolder);
        uploadTimeFolder = uploadTimeFolder.replaceAll("\\\\", "/");
        // LogUtils.debug("==> uploadTimeFolder: {}", uploadTimeFolder);

        File folder = new File(uploadTimeFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String completeFilePath = uploadTimeFolder + File.separator + fileName;
        // LogUtils.debug("completeFilePath: {}", completeFilePath);
        completeFilePath = completeFilePath.replaceAll("\\\\", "/");
        // LogUtils.debug("==> completeFilePath: {}", completeFilePath);

        int len = 0;
        FileOutputStream fileOutputStream = new FileOutputStream(completeFilePath);
        while ((len = inputStream.read()) != -1) {
            fileOutputStream.write(len);
        }
        fileOutputStream.close();
        inputStream.close();

        String url = completeFilePath.substring(completeFilePath.indexOf("upload") + "upload".length());

        HashMap<String, Object> map = new HashMap<>();
        map.put("url", getBaseUrl("files") + url);

        return ResponseEntity.ok().body(map);
    }

    @GetMapping("/{yyyy}/{MM}/{dd}/{filename}")
    public void downloadFile(@PathVariable("yyyy") String year,
                             @PathVariable("MM") String month,
                             @PathVariable("dd") String day,
                             @PathVariable("filename") String filename,
                             HttpServletResponse response) throws IOException {
        // http://xxx/files/2024/05/21/axx.png

        // 根据文件的唯一标识码获取文件
        File preparedFile = new File(uploadFolder + File.separator +
                year + File.separator +
                month + File.separator +
                day + File.separator +
                filename);
        if (!preparedFile.exists()) {
            LogUtils.error("download file not existed: {}", preparedFile.getAbsolutePath());
            return;
        }
        // 设置输出流的格式
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));
        // 任意类型的二进制流数据
        response.setContentType("application/octet-stream");
        // 读取文件的字节流
        os.write(Files.readAllBytes(preparedFile.toPath()));
        os.flush();
        os.close();
    }


}
