package ru.yandex.practicum.telemery.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.telemery.analyzer.model.Scenario;

import java.util.List;
import java.util.Optional;

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {

    Optional<Scenario> findByHubIdAndName(String hubId, String name);

    List<Scenario> findAllByHubId(String hubId);
}
