package ru.yandex.practicum.dto.grpc.hub;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface GrpcHubEvent {
    HubEventProto.PayloadCase getMessageType();
    void handle(HubEventProto event);
}
