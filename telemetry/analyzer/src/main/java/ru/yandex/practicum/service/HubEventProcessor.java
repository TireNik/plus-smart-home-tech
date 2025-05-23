package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.KafkaHubEventConsumer;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.*;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HubEventProcessor implements Runnable {

    KafkaHubEventConsumer consumer;
    SensorRepository sensorRepository;
    ScenarioRepository scenarioRepository;
    ConditionRepository conditionRepository;
    ActionRepository actionRepository;
    ScenarioConditionRepository scenarioConditionRepository;
    ScenarioActionRepository scenarioActionRepository;

    @PostConstruct
    public void subscribe() {
        log.info("Подписка на топик telemetry.hubs.v1");
        consumer.subscribe(List.of("telemetry.hubs.v1"));
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            var records = consumer.poll(Duration.ofMillis(100));
            for (var record : records) {
                try {
                    process(record.value());
                } catch (Exception e) {
                    log.error("Ошибка при обработке события: {}", e.getMessage(), e);
                }
            }
            consumer.commit();
        }
        consumer.close();
    }

    private void process(HubEventAvro event) {
        switch (event.getPayload().getClass().getSimpleName()) {
            case "DeviceAddedEventAvro" -> handleDeviceAdded(event);
            case "DeviceRemovedEventAvro" -> handleDeviceRemoved(event);
            case "ScenarioAddedEventAvro" -> handleScenarioAdded(event);
            case "ScenarioRemovedEventAvro" -> handleScenarioRemoved(event);
            default -> log.warn("Необработанный тип события: {}", event.getPayload().getClass().getSimpleName());
        }
    }

    private void handleDeviceAdded(HubEventAvro event) {
        var data = (DeviceAddedEventAvro) event.getPayload();
        sensorRepository.save(new Sensor(data.getId(), event.getHubId()));
        log.info("Добавлен сенсор: {}", data.getId());
    }

    private void handleDeviceRemoved(HubEventAvro event) {
        var data = (DeviceRemovedEventAvro) event.getPayload();
        sensorRepository.deleteById(data.getId());
        log.info("Удалён сенсор: {}", data.getId());
    }

    private void handleScenarioAdded(HubEventAvro event) {
        var data = (ScenarioAddedEventAvro) event.getPayload();
        var scenarioName = data.getName();
        var hubId = event.getHubId();

        if (scenarioRepository.findByHubIdAndName(hubId, scenarioName).isPresent()) {
            log.warn("Сценарий уже существует: {} для хаба {}", scenarioName, hubId);
            return;
        }

        var scenario = scenarioRepository.save(new Scenario(hubId, scenarioName));
        log.info("Добавлен сценарий: {} (ID: {})", scenarioName, scenario.getId());

        for (var cond : data.getConditions()) {
            Object avroValue = cond.getValue();
            final Integer conditionValue;

            if (avroValue instanceof Boolean boolValue) {
                conditionValue = boolValue ? 1 : 0;
            } else if (avroValue instanceof Integer intValue) {
                conditionValue = intValue;
            } else if (avroValue != null) {
                log.warn("Неожиданный тип значения условия: {} ({})", avroValue, avroValue.getClass().getName());
                conditionValue = null;
            } else {
                conditionValue = null;
            }

            sensorRepository.findById(cond.getSensorId()).ifPresentOrElse(sensor -> {
                var savedCondition = conditionRepository.save(
                        new Condition(cond.getType().name(), cond.getOperation().name(), conditionValue)
                );
                scenarioConditionRepository.save(new ScenarioCondition(scenario, sensor, savedCondition));
                log.debug("Условие добавлено: sensorId={}, type={}, op={}, value={}",
                        cond.getSensorId(), cond.getType(), cond.getOperation(), conditionValue);
            }, () -> log.error("Сенсор не найден для условия: sensorId={}", cond.getSensorId()));
        }

        for (var act : data.getActions()) {
            Integer actionValue = act.getValue();

            sensorRepository.findById(act.getSensorId()).ifPresentOrElse(sensor -> {
                var savedAction = actionRepository.save(
                        new Action(act.getType().name(), actionValue)
                );
                scenarioActionRepository.save(new ScenarioAction(scenario, sensor, savedAction));
                log.debug("Действие добавлено: sensorId={}, type={}, value={}",
                        act.getSensorId(), act.getType(), actionValue);
            }, () -> log.error("Сенсор не найден для действия: sensorId={}", act.getSensorId()));
        }

        log.info("Завершено добавление сценария '{}'", data.getName());
    }


    private void handleScenarioRemoved(HubEventAvro event) {
        var data = (ScenarioRemovedEventAvro) event.getPayload();
        var scenario = scenarioRepository.findByHubIdAndName(event.getHubId(), data.getName())
                .orElseThrow();
        scenarioRepository.delete(scenario);
        log.info("Удалён сценарий: {}", data.getName());
    }
}