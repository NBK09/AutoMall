package com.auto.mall.ad.service;

import com.auto.mall.ad.dto.AdResponse;
import com.auto.mall.ad.entity.Ad;
import com.auto.mall.ad.entity.Favorite;
import com.auto.mall.ad.repository.AdRepository;
import com.auto.mall.ad.repository.FavoriteRepository;
import com.auto.mall.user.entity.User;
import com.auto.mall.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final AdRepository adRepository;
    private final AdService adService;

    public void add(Long userId, Long adId) {
        if (favoriteRepository.existsByUserIdAndAdId(userId, adId)) {
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new EntityNotFoundException("Ad not found"));

        favoriteRepository.save(Favorite.builder().user(user).ad(ad).build());
    }

    public void remove(Long userId, Long adId) {
        favoriteRepository.findByUserIdAndAdId(userId, adId)
                .ifPresent(favoriteRepository::delete);
    }

    @Transactional(readOnly = true)
    public List<AdResponse> list(Long userId) {
        return favoriteRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(Favorite::getAd)
                .map(ad -> adService.mapSummary(ad, userId))
                .toList();
    }
}
