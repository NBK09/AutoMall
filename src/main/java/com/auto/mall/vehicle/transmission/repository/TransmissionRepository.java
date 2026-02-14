package com.auto.mall.vehicle.transmission.repository;

import com.auto.mall.vehicle.transmission.entity.Transmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransmissionRepository extends JpaRepository<Transmission, Long> {
    List<Transmission> findAllByOrderByNameAsc();
    boolean existsByName(String name);
}
