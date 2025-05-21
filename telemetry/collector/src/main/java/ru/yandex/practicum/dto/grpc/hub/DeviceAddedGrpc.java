package ru.yandex.practicum.dto.grpc.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.mappers.GrpcHubEventMapper;

@Component
@RequiredArgsConstructor
public class DeviceAddedGrpc implements GrpcHubEvent {
    private final KafkaEventProducer producer;
    private static final String TOPIC = "telemetry.hubs.v1";

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        var avro = GrpcHubEventMapper.toAvro(event);
        producer.send(TOPIC, event.getHubId(), avro);
    }
}