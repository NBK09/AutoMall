package com.auto.mall.vehicle.brand.repository;

import com.auto.mall.geo.entity.Country;
import com.auto.mall.vehicle.brand.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    List<Brand> findByActiveTrueOrderByNameAsc();
    Optional<Brand> findByName(String name);
}
