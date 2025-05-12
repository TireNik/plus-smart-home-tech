package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.mappers.SensorEventMapper;
import ru.yandex.practicum.model.SensorEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service("sensorEventService")
@RequiredArgsConstructor
public class SensorEventService implements EventService {

    private static final String TOPIC = "telemetry.sensors.v1";

    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;

    @Override
    public void processEvent(Object event) {
        if (event instanceof SensorEvent sensorEvent) {
            SensorEventAvro avro = SensorEventMapper.toAvro(sensorEvent);
            kafkaTemplate.send(TOPIC, sensorEvent.getId(), avro);
        } else {
            throw new IllegalArgumentException("Неверный тип события: ожидается SensorEvent");
        }
    }
}