package com.auto.mall.auth.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecureController {

    @GetMapping("/api/secure/test")
    public String test() {
        return "JWT работает, доступ разрешён!";
    }
}