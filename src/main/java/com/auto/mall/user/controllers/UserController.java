package com.auto.mall.user.controllers;

import com.auto.mall.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/me")
    public String me(@AuthenticationPrincipal CustomUserPrincipal user) {
        return "Current user id: " + user.getUserId();
    }
}
