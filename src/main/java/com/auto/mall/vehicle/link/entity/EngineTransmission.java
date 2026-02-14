package com.auto.mall.vehicle.link.entity;

import com.auto.mall.vehicle.engine.entity.Engine;
import com.auto.mall.vehicle.transmission.entity.Transmission;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "engine_transmissions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EngineTransmission {

    @EmbeddedId
    private EngineTransmissionId id;

    @ManyToOne
    @MapsId("engineId")
    private Engine engine;

    @ManyToOne
    @MapsId("transmissionId")
    private Transmission transmission;
}
