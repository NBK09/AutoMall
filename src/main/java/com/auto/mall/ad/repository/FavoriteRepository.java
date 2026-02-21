package com.auto.mall.ad.repository;

import com.auto.mall.ad.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserIdAndAdId(Long userId, Long adId);

    Optional<Favorite> findByUserIdAndAdId(Long userId, Long adId);

    List<Favorite> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
