package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.telemetry.collector.model.Action;

public interface ActionRepository extends JpaRepository<Action, Long> {
}