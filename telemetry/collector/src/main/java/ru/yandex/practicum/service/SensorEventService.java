package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.mappers.SensorEventMapper;
import ru.yandex.practicum.model.SensorEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service("sensorEventService")
public class SensorEventService implements EventService {

    private static final String TOPIC = "telemetry.sensors.v1";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SensorEventMapper mapper;

    public SensorEventService(@Qualifier("kafkaTemplate") KafkaTemplate<String, Object> kafkaTemplate,
                              SensorEventMapper mapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }

    @Override
    public void processEvent(Object event) {
        if (event instanceof SensorEvent sensorEvent) {
            SensorEventAvro avro = mapper.mapToAvro(sensorEvent);
            kafkaTemplate.send(TOPIC, avro.getId().toString(), avro);
        }
    }
}