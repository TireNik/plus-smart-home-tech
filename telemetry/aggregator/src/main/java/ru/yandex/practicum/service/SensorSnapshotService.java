package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class SensorSnapshotService {

    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateSnapshot(SensorEventAvro event) {
        String hubId = event.getHubId();
        String sensorId = event.getId();
        long newTs = event.getTimestamp();

        log.debug("Обновление снапшота: hubId={}, sensorId={}, timestamp={}", hubId, sensorId, newTs);

        SensorsSnapshotAvro snapshot = snapshots.computeIfAbsent(hubId, id -> {
            log.info("Создан новый снапшот для хаба: {}", hubId);
            SensorsSnapshotAvro snap = new SensorsSnapshotAvro();
            snap.setHubId(hubId);
            snap.setTimestamp(Instant.ofEpochMilli(newTs));
            snap.setSensorsState(new HashMap<>());
            return snap;
        });

        var sensorsState = snapshot.getSensorsState();
        var oldState = sensorsState.get(sensorId);

        if (oldState != null) {
            long oldTs = oldState.getTimestamp().toEpochMilli();

            if (oldTs > newTs) {
                log.debug("Пропущено устаревшее событие от датчика {}: старый ts={}, новый ts={}", sensorId, oldTs, newTs);
                return Optional.empty();
            }

            if (isUnchanged(oldState.getData(), event.getPayload())) {
                log.debug("Данные сенсора {} не изменились", sensorId);
                return Optional.empty();
            }

            log.debug("Обновление состояния сенсора {}: старый ts={}, новый ts={}", sensorId, oldTs, newTs);

        } else {
            log.debug("Добавление нового сенсора в снапшот: {}", sensorId);
        }

        SensorStateAvro newState = new SensorStateAvro();
        newState.setTimestamp(Instant.ofEpochMilli(newTs));
        newState.setData(event.getPayload());
        sensorsState.put(sensorId, newState);
        snapshot.setTimestamp(Instant.ofEpochMilli(newTs));

        log.info("Сенсор {} обновлён. Снапшот хаба {}: timestamp={}", sensorId, hubId, snapshot.getTimestamp());
        return Optional.of(snapshot);
    }

    private boolean isUnchanged(Object oldPayload, Object newPayload) {
        if (!oldPayload.getClass().equals(newPayload.getClass())) {
            log.warn("Типы payload не совпадают: old={}, new={}", oldPayload.getClass(), newPayload.getClass());
            return false;
        }

        if (oldPayload instanceof ClimateSensorAvro oldC && newPayload instanceof ClimateSensorAvro newC) {
            return oldC.getTemperatureC() == newC.getTemperatureC()
                    && oldC.getHumidity() == newC.getHumidity()
                    && oldC.getCo2Level() == newC.getCo2Level();
        }

        if (oldPayload instanceof LightSensorAvro oldL && newPayload instanceof LightSensorAvro newL) {
            return oldL.getLinkQuality() == newL.getLinkQuality()
                    && oldL.getLuminosity() == newL.getLuminosity();
        }

        if (oldPayload instanceof MotionSensorAvro oldM && newPayload instanceof MotionSensorAvro newM) {
            return oldM.getLinkQuality() == newM.getLinkQuality()
                    && oldM.getMotion() == newM.getMotion()
                    && oldM.getVoltage() == newM.getVoltage();
        }

        if (oldPayload instanceof SwitchSensorAvro oldS && newPayload instanceof SwitchSensorAvro newS) {
            return oldS.getState() == newS.getState();
        }

        if (oldPayload instanceof TemperatureSensorAvro oldT && newPayload instanceof TemperatureSensorAvro newT) {
            return oldT.getTemperatureC() == newT.getTemperatureC()
                    && oldT.getTemperatureF() == newT.getTemperatureF();
        }

        log.warn("Необрабатываемый тип сенсора: {}", newPayload.getClass().getSimpleName());
        return false;
    }
}