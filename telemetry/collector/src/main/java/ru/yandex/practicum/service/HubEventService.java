package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.mappers.HubEventMapper;
import ru.yandex.practicum.model.HubEvent;

@Service("hubEventService")
public class HubEventService implements EventService {

    private static final String TOPIC = "telemetry.hubs.v1";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final HubEventMapper mapper;

    public HubEventService(@Qualifier("kafkaTemplate") KafkaTemplate<String, Object> kafkaTemplate,
                           HubEventMapper mapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }

    @Override
    public void processEvent(Object event) {
        if (event instanceof HubEvent hubEvent) {
            HubEventAvro avro = mapper.mapToAvro(hubEvent);
            kafkaTemplate.send(TOPIC, avro.getHubId().toString(), avro);
        }
    }
}