package ru.yandex.practicum.telemery.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.telemery.analyzer.model.Action;

public interface ActionRepository extends JpaRepository<Action, Long> {
}
