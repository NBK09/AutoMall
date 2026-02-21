//package com.auto.mall.ad.repository;
//
//import com.auto.mall.ad.entity.AdPhoto;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//
//public interface AdPhotoRepository extends JpaRepository<AdPhoto, Long> {
//    long countByAdId(Long adId);
//    List<AdPhoto> findByAdIdOrderBySortOrderAsc(Long adId);
//}