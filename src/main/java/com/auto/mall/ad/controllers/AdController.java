package com.auto.mall.ad.controllers;

import com.auto.mall.ad.Enum.AdStatus;
import com.auto.mall.ad.dto.*;
import com.auto.mall.ad.service.AdService;
import com.auto.mall.security.CustomUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ads")
public class AdController {

    private final AdService adService;

    @PostMapping
    public AdResponse create(
            @Valid @RequestBody CreateAdRequest request,
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {
        return adService.createAd(request, user.getUserId());
    }

    @GetMapping
    public List<AdResponse> getAllActive() {
        return adService.getAllActiveAds();
    }

    @GetMapping("/{id}")
    public AdDetailsResponse getDetails(@PathVariable Long id) {
        return adService.getDetails(id);
    }

    @GetMapping("/{id}/edit")
    public AdEditResponse getForEdit(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {
        return adService.getForEdit(id, user.getUserId());
    }

    @PutMapping("/{id}")
    public AdDetailsResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAdRequest request,
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {
        return adService.updateAd(id, user.getUserId(), request);
    }

    @GetMapping("/my")
    public List<AdResponse> getMy(
            @RequestParam(required = false) AdStatus status,
            Authentication authentication
    ) {
        Long userId = Long.valueOf(authentication.getName());

        if (status == null) {
            status = AdStatus.ACTIVE;
        }

        return adService.getMyByStatus(userId, status);
    }

    @PutMapping("/{id}/archive")
    public void archive(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {
        adService.archive(id, user.getUserId());
    }

    @PutMapping("/{id}/restore")
    public void restore(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {
        adService.restore(id, user.getUserId());
    }
}
