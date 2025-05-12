package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.mappers.HubEventMapper;
import ru.yandex.practicum.model.HubEvent;

@Service("hubEventService")
@RequiredArgsConstructor
public class HubEventService implements EventService {

    private static final String TOPIC = "telemetry.hubs.v1";

    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;

    @Override
    public void processEvent(Object event) {
        if (event instanceof HubEvent hubEvent) {
            HubEventAvro avro = HubEventMapper.toAvro(hubEvent);
            kafkaTemplate.send(TOPIC, hubEvent.getHubId(), avro);
        } else {
            throw new IllegalArgumentException("Неверный тип события: ожидается HubEvent");
        }
    }
}