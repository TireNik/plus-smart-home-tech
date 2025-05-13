package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.config.KafkaConfig;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.mappers.SensorEventMapper;
import ru.yandex.practicum.model.SensorEvent;
import org.springframework.stereotype.Service;

@Service("sensorEventService")
@RequiredArgsConstructor
public class SensorEventService implements EventService {
    final KafkaConfig kafkaConfig;

    @Override
    public void processEvent(Object event) {
        if (event instanceof SensorEvent sensorEvent) {
            SensorEventAvro avro = SensorEventMapper.toAvro(sensorEvent);
            kafkaConfig.getProducer().send(
                    new ProducerRecord<>(kafkaConfig.getSensorEventsTopic(), sensorEvent.getHubId(), avro)
            );
        } else {
            throw new IllegalArgumentException("Неверный тип события: ожидается SensorEvent");
        }
    }

}