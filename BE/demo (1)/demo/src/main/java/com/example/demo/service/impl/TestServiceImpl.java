package com.example.demo.service.impl;

import com.example.demo.service.TestService;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {

    @Override
    public String test() {
        return "Service working!";
    }
}
