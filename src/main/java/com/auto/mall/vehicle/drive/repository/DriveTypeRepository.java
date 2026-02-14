package com.auto.mall.vehicle.drive.repository;

import com.auto.mall.vehicle.drive.entity.DriveType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DriveTypeRepository extends JpaRepository<DriveType, Long> {
    List<DriveType> findAllByOrderByNameAsc();
    boolean existsByName(String name);
}
