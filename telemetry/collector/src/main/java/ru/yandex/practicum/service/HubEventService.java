package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.config.KafkaConfig;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.mappers.HubEventMapper;
import ru.yandex.practicum.model.HubEvent;

@Service("hubEventService")
@RequiredArgsConstructor
public class HubEventService implements EventService {

    final KafkaConfig kafkaConfig;

    @Override
    public void processEvent(Object event) {
        if (event instanceof HubEvent hubEvent) {
            HubEventAvro avro = HubEventMapper.toAvro(hubEvent);
            kafkaConfig.getProducer().send(
                    new ProducerRecord<>(kafkaConfig.getHubEventsTopic(), hubEvent.getHubId(), avro)
            );
        }
    }
}