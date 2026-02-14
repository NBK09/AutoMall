package com.auto.mall.vehicle.model.repository;

import com.auto.mall.vehicle.brand.entity.Brand;
import com.auto.mall.vehicle.model.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ModelRepository extends JpaRepository<Model, Long> {
    List<Model> findByBrandIdAndActiveTrueOrderByNameAsc(Long brandId);
    Optional<Model> findByNameAndBrand(String name, Brand brand);
}
