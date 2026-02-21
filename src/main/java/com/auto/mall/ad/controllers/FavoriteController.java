package com.auto.mall.ad.controllers;

import com.auto.mall.ad.dto.AdResponse;
import com.auto.mall.ad.service.FavoriteService;
import com.auto.mall.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{adId}")
    public void add(
            @PathVariable Long adId,
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {
        favoriteService.add(user.getUserId(), adId);
    }

    @DeleteMapping("/{adId}")
    public void remove(
            @PathVariable Long adId,
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {
        favoriteService.remove(user.getUserId(), adId);
    }

    @GetMapping
    public List<AdResponse> list(@AuthenticationPrincipal CustomUserPrincipal user) {
        return favoriteService.list(user.getUserId());
    }
}
