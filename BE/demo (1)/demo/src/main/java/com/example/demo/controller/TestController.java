package com.example.demo.controller;

import com.example.demo.service.TestService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping
    public String test() {
        return testService.test();
    }
}
