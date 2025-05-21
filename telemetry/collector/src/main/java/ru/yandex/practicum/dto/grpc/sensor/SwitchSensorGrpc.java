package ru.yandex.practicum.dto.grpc.sensor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.mappers.GrpcSensorEventMapper;

@Component
@RequiredArgsConstructor
public class SwitchSensorGrpc implements GrpcSensorEvent{

    private final KafkaEventProducer producer;
    private static final String TOPIC = "telemetry.sensors.v1";

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        var avro = GrpcSensorEventMapper.toAvro(event);
        producer.send(TOPIC, event.getId(), avro);
    }
}