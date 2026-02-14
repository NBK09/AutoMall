package com.auto.mall.auth.controllers;

import com.auto.mall.auth.DTO.TelegramUser;
import com.auto.mall.auth.service.JwtService;
import com.auto.mall.auth.service.TelegramAuthService;
import com.auto.mall.user.entity.User;
import com.auto.mall.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class TelegramAuthController {
    private final TelegramAuthService telegramAuthService;
    private final UserService userService;
    private final JwtService jwtService;

    public TelegramAuthController(TelegramAuthService telegramAuthService,
                                  UserService userService, JwtService jwtService) {
        this.telegramAuthService = telegramAuthService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/telegram")
    public ResponseEntity<?> auth(@RequestBody TelegramInitDataRequest body) {

        String initData = body.initData(); // строка из Telegram WebApp

        User user = telegramAuthService.authenticate(initData);

        String token = jwtService.generateToken(
                user.getId(),
                user.getTelegramId()
        );

        return ResponseEntity.ok(Map.of(
                "token", token,
                "user", user
        ));
    }
    public record TelegramInitDataRequest(String initData) {}
}
