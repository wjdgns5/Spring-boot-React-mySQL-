package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/private")
public class TestController {

    // http://localhost:8080/api/private/hello
    @GetMapping("/hello")
    public String publicHello() {
        return "인증된 사용자";
    }

}
