package ru.yandex.practicum.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassMapping;
import ru.yandex.practicum.dto.sensor.*;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.SensorEvent;
import ru.yandex.practicum.service.SensorEventService;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface SensorEventMapper {
    @Mapping(target = "payload", source = ".", qualifiedByName = "mapClass")
    SensorEventAvro mapToAvro(SensorEvent event);

    @Named("mapClass")
    @SubclassMapping(source = ClimateSensorEvent.class, target = ClimateSensorAvro.class)
    @SubclassMapping(source = LightSensorEvent.class, target = LightSensorAvro.class)
    @SubclassMapping(source = MotionSensorEvent.class, target = MotionSensorAvro.class)
    @SubclassMapping(source = SwitchSensorEvent.class, target = SwitchSensorAvro.class)
    @SubclassMapping(source = TemperatureSensorEvent.class, target = TemperatureSensorAvro.class)
    Object mapClass(SensorEvent event);

    ClimateSensorAvro mapToAvro(ClimateSensorEvent event);
    LightSensorAvro mapToAvro(LightSensorEvent event);
    MotionSensorAvro mapToAvro(MotionSensorEvent event);
    SwitchSensorAvro mapToAvro(SwitchSensorEvent event);
    TemperatureSensorAvro mapToAvro(TemperatureSensorEvent event);

    default long map(Instant value) {
        return value.toEpochMilli();
    }
}