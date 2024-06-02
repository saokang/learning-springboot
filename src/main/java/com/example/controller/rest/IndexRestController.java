package com.example.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/rest")
public class IndexRestController {

    @GetMapping("/index")
    public String index() {
        return "Rest / Index";
    }
}
