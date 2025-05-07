package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.model.SensorEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service("sensorEventService")
public class SensorEventService implements EventService {

    private static final String TOPIC = "telemetry.sensors.v1";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public SensorEventService(@Qualifier("kafkaTemplate") KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void processEvent(Object event) {
        if (event instanceof SensorEvent sensorEvent) {
            kafkaTemplate.send(TOPIC, sensorEvent.getId(), sensorEvent);
        }
    }
}