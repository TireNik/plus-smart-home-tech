package ru.yandex.practicum.telemery.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.telemery.analyzer.model.ScenarioCondition;
import ru.yandex.practicum.telemery.analyzer.model.ScenarioConditionId;

import java.util.List;

public interface ScenarioConditionRepository extends JpaRepository<ScenarioCondition, ScenarioConditionId> {

    List<ScenarioCondition> findAllByScenarioId(Long scenarioId);
}
