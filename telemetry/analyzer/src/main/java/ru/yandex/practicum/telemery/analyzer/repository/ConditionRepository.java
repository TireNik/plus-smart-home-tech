package ru.yandex.practicum.telemery.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.telemery.analyzer.model.Condition;

public interface ConditionRepository extends JpaRepository<Condition, Long> {
}
