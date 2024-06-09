package com.example;

import com.example.demo.PojoDemo;
import com.example.demo.learning02_aspect.UseLogAspectDemo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PrimitiveIterator;
import java.util.stream.Collectors;

@SpringBootTest
class LearningSpringbootApplicationTests {

    @Test
    void contextLoads() {
        System.out.println(1 << 10);
    }


    /**
     * 2024-06-06 00:45:27 687 INFO --- [main] Executing method: getLog
     * 2024-06-06 00:45:27 688 INFO --- [main] LogExecution value: call getLog method
     * 2024-06-06 00:45:27 688 INFO --- [main] Arguments: [375336]
     * id: 375336
     * 2024-06-06 00:45:27 688 INFO --- [main] Method getLog returned with value: 375336
     * 2024-06-06 00:45:27 690 INFO --- [main] Method String com.example.demo.aspect.LogServiceImplDemo.getLog(Integer) executed in 10 ms
     * 2024-06-06 00:45:27 690 INFO --- [main] Executing method: getLogInfo
     * 2024-06-06 00:45:27 690 INFO --- [main] LogExecution value: call getLogInfo method
     * 2024-06-06 00:45:27 690 INFO --- [main] Arguments: [hwx1302778, huangzhikang]
     * id: hwx1302778 logName: huangzhikang
     * 2024-06-06 00:45:27 690 INFO --- [main] Method getLogInfo returned with value: {logName=huangzhikang, id=hwx1302778}
     * 2024-06-06 00:45:27 690 INFO --- [main] Method Map com.example.demo.aspect.LogServiceImplDemo.getLogInfo(String,String) executed in 0 ms
     * 2024-06-06 00:45:27 690 INFO --- [main] Executing method: getLogWithException
     * 2024-06-06 00:45:27 690 INFO --- [main] LogExecution value:
     * 2024-06-06 00:45:27 690 INFO --- [main] Arguments: empty args
     * 2024-06-06 00:45:27 691 ERROR --- [main] Method getLogWithException thrown exception: one more failed thing...
     * 2024-06-06 00:45:27 691 ERROR --- [main] Method boolean com.example.demo.aspect.LogServiceImplDemo.getLogWithException() failed in 1 ms
     */

    @Autowired
    UseLogAspectDemo logService;

    @Test
    void test4AspectLog() {
        String log = logService.getLog(375336);
        Map<String, String> logInfo = logService.getLogInfo("hwx1302778", "huangzhikang");
        boolean bool = logService.getLogWithException();
    }


    /**
     * 将obj转换成map
     */
    public Map<String, Object> beanToMap(Object obj) {
        Map<String, Object> map = new HashMap<>();
        Class<?> objClass = obj.getClass();
        Field[] declaredFields = objClass.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            String filename = field.getName();
            Object val = null;
            try {
                val = field.get(obj);
            } catch (Exception ex) {

            }
            map.put(filename, val);
        }
        return map;
    }

    @Test
    public void test4beanToMap() {
        PojoDemo pojoDemo = new PojoDemo().id(375336).username("huangzhikang");
        // PojoDemo pojoDemo = new PojoDemo().id(375336).username("huangzhikang").password("hwx1302278");
        Map<String, Object> bean = beanToMap(pojoDemo);
        bean.entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        });
    }


    @Test
    public void test4FileWalkMethod() throws IOException {
        String projectDir = System.getProperty("user.dir");

        // jdk 1.8
        File project = new File(projectDir);
        Arrays.stream(project.listFiles())
                .map(File::getAbsoluteFile)
                .collect(Collectors.toList())
                .forEach(System.out::println);

        // jdk 1.8+
        System.out.println("===== 1.8+ =====");
        Path projectPath = Path.of(projectDir);
        Files.walk(projectPath, 2)
                .filter(path -> !path.equals(projectPath))
                .map(projectPath::relativize)
                .filter(path -> !Files.isDirectory(path))
                .collect(Collectors.toList())
                .forEach(System.out::println);

    }

}
