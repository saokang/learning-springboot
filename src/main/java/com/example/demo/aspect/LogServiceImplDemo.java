package com.example.demo.aspect;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
@Service
public class LogServiceImplDemo {

    @Log(value = "call getLog method")
    public String getLog(Integer id) {
        System.out.println("id: " + id);
        return String.valueOf(id);
    }


    @Log(value = "call getLogInfo method")
    public Map<String, String> getLogInfo(String id, String logName) {
        System.out.println("id: " + id + " logName: " + logName);
        HashMap<String, String> map = new HashMap<>();
        map.put("id", id);
        map.put("logName", logName);
        return map;
    }
    @Log
    public boolean getLogWithException() {
        throw new RuntimeException("one more failed thing...");
    }
}
