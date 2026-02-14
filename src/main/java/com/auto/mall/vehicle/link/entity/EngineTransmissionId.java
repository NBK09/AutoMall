package com.auto.mall.vehicle.link.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EngineTransmissionId implements Serializable {

    private Long engineId;
    private Long transmissionId;
}
