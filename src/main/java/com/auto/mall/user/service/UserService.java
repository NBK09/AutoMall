package com.auto.mall.user.service;

import com.auto.mall.auth.DTO.TelegramUser;
import com.auto.mall.user.entity.User;
import com.auto.mall.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveOrUpdate(TelegramUser tgUser) {

        return userRepository
                .findByTelegramId(tgUser.getId())
                .map(existing -> {
                    existing.setFirstName(tgUser.getFirst_name());
                    existing.setLastName(tgUser.getLast_name());
                    existing.setUsername(tgUser.getUsername());
                    existing.setLanguageCode(tgUser.getLanguage_code());
                    existing.setIsPremium(tgUser.getIs_premium());
                    return userRepository.save(existing);
                })
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setTelegramId(tgUser.getId());
                    newUser.setFirstName(tgUser.getFirst_name());
                    newUser.setLastName(tgUser.getLast_name());
                    newUser.setUsername(tgUser.getUsername());
                    newUser.setLanguageCode(tgUser.getLanguage_code());
                    newUser.setIsPremium(tgUser.getIs_premium());
                    return userRepository.save(newUser);
                });
    }
}