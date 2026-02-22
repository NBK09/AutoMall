package com.auto.mall.ad.entity;

import com.auto.mall.ad.Enum.AdStatus;
import com.auto.mall.geo.entity.City;
import com.auto.mall.user.entity.User;
import com.auto.mall.vehicle.brand.entity.Brand;
import com.auto.mall.vehicle.drive.entity.DriveType;
import com.auto.mall.vehicle.engine.entity.Engine;
import com.auto.mall.vehicle.generation.entity.Generation;
import com.auto.mall.vehicle.model.entity.Model;
import com.auto.mall.vehicle.transmission.entity.Transmission;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(name = "ads")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== relations =====
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Brand brand;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Model model;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Generation generation;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Engine engine;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Transmission transmission;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private DriveType driveType;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private City city;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<AdPhoto> photos = new ArrayList<>();

    // ===== fields =====
    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer mileage;

    @Column(nullable = false)
    private String color;

    private String vin;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false, length = 5000)
    private String description;

    // UI/логика
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdStatus status; // DRAFT/ACTIVE/ARCHIVED

    @Column(nullable = false)
    private Boolean active; // можно оставить для фильтра "только активные"

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;

        if (status == null) status = AdStatus.ACTIVE;
        if (active == null) active = true;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
