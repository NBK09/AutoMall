package com.auto.mall.ad.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ad_photos",
        uniqueConstraints = @UniqueConstraint(name = "uk_ad_photo_object_key", columnNames = "object_key"))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AdPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_id", nullable = false)
    private Ad ad;

    @Column(name = "object_key", nullable = false, length = 500)
    private String objectKey;

    @Column(name = "url", nullable = false, length = 1000)
    private String url;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}