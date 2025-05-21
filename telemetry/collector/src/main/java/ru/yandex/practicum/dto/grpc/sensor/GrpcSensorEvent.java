package ru.yandex.practicum.dto.grpc.sensor;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface GrpcSensorEvent {
    SensorEventProto.PayloadCase getMessageType();
    void handle(SensorEventProto event);
}
