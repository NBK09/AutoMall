package com.auto.mall.ad.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "ad_photos",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_ad_photos_s3_key", columnNames = "s3_key")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_id", nullable = false)
    private Ad ad;

    @Column(name = "s3_key", nullable = false, unique = true)
    private String s3Key;

    @Column(name = "public_url")
    private String publicUrl;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "is_main", nullable = false)
    private Boolean isMain;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (sortOrder == null) {
            sortOrder = 0;
        }
        if (isMain == null) {
            isMain = false;
        }
    }
}
