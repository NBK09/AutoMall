package com.auto.mall.ad.repository;

import com.auto.mall.ad.Enum.AdStatus;
import com.auto.mall.ad.entity.Ad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdRepository extends JpaRepository<Ad, Long> {

    List<Ad> findByStatusOrderByCreatedAtDesc(AdStatus status);

    List<Ad> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, AdStatus status);
}