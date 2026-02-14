package com.auto.mall.auth.service;

import com.auto.mall.auth.DTO.TelegramUser;
import com.auto.mall.user.entity.User;
import com.auto.mall.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class TelegramAuthService {

    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TelegramAuthService(UserService userService) {
        this.userService = userService;
    }

    public User authenticate(String initData) {

        Map<String, String> data = parseInitData(initData);

        // 🔐 Проверка времени (защита от replay)
        validateAuthDate(data);

        // 👤 Парсим TelegramUser
        TelegramUser telegramUser = extractTelegramUser(data);

        // 💾 Сохраняем или обновляем в БД
        return userService.saveOrUpdate(telegramUser);
    }

    private Map<String, String> parseInitData(String initData) {
        Map<String, String> data = new HashMap<>();

        for (String pair : initData.split("&")) {
            int idx = pair.indexOf('=');
            if (idx > 0) {
                String key = pair.substring(0, idx);
                String value = pair.substring(idx + 1);
                data.put(key, value);
            }
        }

        return data;
    }

    private void validateAuthDate(Map<String, String> data) {
        String authDateStr = data.get("auth_date");

        if (authDateStr == null) {
            throw new RuntimeException("Missing auth_date");
        }

        long authDate = Long.parseLong(authDateStr);
        long now = System.currentTimeMillis() / 1000;

        if (now - authDate > 86400) { // 24 часа
            throw new RuntimeException("Telegram auth expired");
        }
    }

    private TelegramUser extractTelegramUser(Map<String, String> data) {
        try {
            String encodedUser = data.get("user");

            if (encodedUser == null) {
                throw new RuntimeException("No user field in initData");
            }

            String decodedUser = URLDecoder.decode(
                    encodedUser,
                    StandardCharsets.UTF_8
            );

            return objectMapper.readValue(decodedUser, TelegramUser.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Telegram user", e);
        }
    }
}