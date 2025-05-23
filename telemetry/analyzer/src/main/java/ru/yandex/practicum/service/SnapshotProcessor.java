package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.client.HubRouterGrpcClient;
import ru.yandex.practicum.kafka.KafkaSnapshotConsumer;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.ScenarioCondition;
import ru.yandex.practicum.repository.ScenarioActionRepository;
import ru.yandex.practicum.repository.ScenarioConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SnapshotProcessor {

    ScenarioRepository scenarioRepository;
    ScenarioConditionRepository scenarioConditionRepository;
    ScenarioActionRepository scenarioActionRepository;
    HubRouterGrpcClient grpcClient;
    KafkaSnapshotConsumer consumer;

    public void start() {
        log.info("Подписка на топик telemetry.snapshots.v1");
        consumer.subscribe(List.of("telemetry.snapshots.v1"));
        while (!Thread.currentThread().isInterrupted()) {
            var records = consumer.poll(Duration.ofMillis(100));
            for (var record : records) {
                try {
                    process(record.value());
                } catch (Exception e) {
                    log.error("Ошибка при обработке снапшота", e);
                }
            }
            consumer.commit();
        }
        consumer.close();
    }

    private void process(SensorsSnapshotAvro snapshot) {
        var hubId = snapshot.getHubId();
        var scenarios = scenarioRepository.findByHubId(hubId);
        for (var scenario : scenarios) {
            var conditions = scenarioConditionRepository.findAllByScenarioId(scenario.getId());
            if (conditionsMet(conditions, snapshot)) {
                var actions = scenarioActionRepository.findAllByScenarioId(scenario.getId());
                for (var action : actions) {
                    grpcClient.sendDeviceAction(hubId, scenario.getName(),
                            action.getSensor().getId(), action.getAction().getValue(),
                            action.getAction().getType());
                }
            }
        }
    }

    private boolean conditionsMet(List<ScenarioCondition> conditions, SensorsSnapshotAvro snapshot) {
        for (ScenarioCondition condition : conditions) {
            var sensorId = condition.getSensor().getId();
            var state = snapshot.getSensorsState().get(sensorId);

            if (state == null || !evaluateCondition(condition.getCondition(), state.getData())) {
                return false;
            }
        }
        return true;
    }

    private boolean evaluateCondition(Condition condition, Object data) {
        String type = condition.getType();

        if (data == null) {
            log.warn("Данные сенсора отсутствуют (data == null) для условия: {}", condition);
            return false;
        }

        log.debug("Тип данных из снапшота: {}", data.getClass().getName());
        String operation = condition.getOperation();
        Integer expectedValue = condition.getValue();

        if (expectedValue == null) {
            log.error("Expected value in condition is null: {}", condition);
            return false;
        }

        log.debug("Проверка условия: type={}, operation={}, expected={}", type, operation, expectedValue);
        switch (type) {
            case "LUMINOSITY" -> {
                log.debug("Проверка типа LUMINOSITY для data={}", data.getClass().getName());
                if (data instanceof LightSensorAvro light) {
                    return compareAndLog("LUMINOSITY", light.getLuminosity(), expectedValue, operation);
                } else {
                    log.warn("Ожидался LightSensorAvro, но пришёл {}", data.getClass().getName());
                    return false;
                }
            }
            case "TEMPERATURE" -> {
                log.debug("Проверка типа TEMPERATURE для data={}", data.getClass().getName());
                if (data instanceof ClimateSensorAvro climate) {
                    return compareAndLog("TEMPERATURE (climate)", climate.getTemperatureC(), expectedValue,
                            operation);
                } else if (data instanceof TemperatureSensorAvro temp) {
                    boolean resultC = compareAndLog("TEMPERATURE (temp, °C)", temp.getTemperatureC(),
                            expectedValue, operation);
                    boolean resultF = compareAndLog("TEMPERATURE (temp, °F)", temp.getTemperatureF(),
                            expectedValue, operation);
                    return resultC || resultF;
                } else {
                    log.warn("Ожидался ClimateSensorAvro или TemperatureSensorAvro, но пришёл {}",
                            data.getClass().getName());
                    return false;
                }
            }
            case "CO2LEVEL" -> {
                log.debug("Проверка типа CO2LEVEL для data={}", data.getClass().getName());
                if (data instanceof ClimateSensorAvro climate) {
                    return compareAndLog("CO2LEVEL", climate.getCo2Level(), expectedValue, operation);
                } else {
                    log.warn("Ожидался ClimateSensorAvro, но пришёл {}", data.getClass().getName());
                    return false;
                }
            }
            case "HUMIDITY" -> {
                log.debug("Проверка типа HUMIDITY для data={}", data.getClass().getName());
                if (data instanceof ClimateSensorAvro climate) {
                    return compareAndLog("HUMIDITY", climate.getHumidity(), expectedValue, operation);
                } else {
                    log.warn("Ожидался ClimateSensorAvro, но пришёл {}", data.getClass().getName());
                    return false;
                }
            }
            case "MOTION" -> {
                log.debug("Проверка типа MOTION для data={}", data.getClass().getName());
                if (data instanceof MotionSensorAvro motion) {
                    return compareAndLog("MOTION", motion.getMotion() ? 1 : 0, expectedValue, operation);
                } else {
                    log.warn("Ожидался MotionSensorAvro, но пришёл {}", data.getClass().getName());
                    return false;
                }
            }
            case "SWITCH" -> {
                log.debug("Проверка типа SWITCH для data={}", data.getClass().getName());
                if (data instanceof SwitchSensorAvro sw) {
                    return compareAndLog("SWITCH", sw.getState() ? 1 : 0, expectedValue, operation);
                } else {
                    log.warn("Ожидался SwitchSensorAvro, но пришёл {}", data.getClass().getName());
                    return false;
                }
            }
            default -> {
                log.warn("Неизвестный тип условия: {}", type);
                return false;
            }
        }
    }

    private boolean compare(int actual, int expected, String operation) {
        return switch (operation) {
            case "EQUALS" -> actual == expected;
            case "GREATER_THAN" -> actual > expected;
            case "LOWER_THAN" -> actual < expected;
            default -> false;
        };
    }

    private boolean compareAndLog(String label, int actual, int expected, String operation) {
        boolean result = compare(actual, expected, operation);
        log.debug("{}: {} {} {} → {}", label, actual, expected, operation, result ? "выполнено" : "не выполнено");
        return result;
    }
}