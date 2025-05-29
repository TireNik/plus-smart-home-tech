package ru.yandex.practicum.telemery.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.telemery.analyzer.model.Sensor;

public interface SensorRepository extends JpaRepository<Sensor, String> {
}
