package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.HubEvent;

@Service("hubEventService")
public class HubEventService implements EventService {

    private static final String TOPIC = "telemetry.hubs.v1";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public HubEventService(@Qualifier("kafkaTemplate") KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void processEvent(Object event) {
        if (event instanceof HubEvent hubEvent) {
            kafkaTemplate.send(TOPIC, hubEvent.getHubId(), hubEvent);
        }
    }
}