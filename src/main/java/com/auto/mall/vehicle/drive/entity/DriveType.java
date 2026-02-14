package com.auto.mall.vehicle.drive.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "drive_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private boolean active;
}
