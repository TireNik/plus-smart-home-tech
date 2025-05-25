package ru.yandex.practicum.telemery.analyzer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = Sensor.TABLE_NAME)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@ToString
public class Sensor {
    public static final String TABLE_NAME = "sensors";
    public static final String ID = "id";
    public static final String HUB_ID = "hub_id";

    @Id
    @Column(name = ID)
    String id;

    @Column(name = HUB_ID)
    String hubId;
}
