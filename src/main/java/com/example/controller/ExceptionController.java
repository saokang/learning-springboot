package com.example.controller;

import com.example.exception.UnknownException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/exception")
public class ExceptionController {

    @GetMapping("/unknown")
    public ResponseEntity<String> unknownException() {
        throw new UnknownException("system unknown exception...");
    }

    @GetMapping("/biz")
    public ResponseEntity<String> businessException() {
        throw new UnknownException("system business exception...");
    }

    @GetMapping("/error")
    public ResponseEntity<String> exception() {
        throw new UnknownException("system error exception...");
    }
}
