package ru.yandex.practicum.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassMapping;
import ru.yandex.practicum.dto.sensor.*;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.SensorEvent;


@Mapper(componentModel = "spring")
public interface SensorEventMapper {

    @Mapping(target = "event", source = ".", qualifiedByName = "mapClass")
    SensorEventAvro mapToAvro(SensorEvent event);

    @Named("mapClass")
    @SubclassMapping(source = ClimateSensorEvent.class, target = ClimateSensorEventAvro.class)
    @SubclassMapping(source = LightSensorEvent.class, target = LightSensorEventAvro.class)
    @SubclassMapping(source = MotionSensorEvent.class, target = MotionSensorEventAvro.class)
    @SubclassMapping(source = SwitchSensorEvent.class, target = SwitchSensorEventAvro.class)
    @SubclassMapping(source = TemperatureSensorEvent.class, target = TemperatureSensorEventAvro.class)
    Object mapClass(SensorEvent event);

    ClimateSensorEventAvro mapToAvro(ClimateSensorEvent event);
    LightSensorEventAvro mapToAvro(LightSensorEvent event);
    MotionSensorEventAvro mapToAvro(MotionSensorEvent event);
    SwitchSensorEventAvro mapToAvro(SwitchSensorEvent event);
    TemperatureSensorEventAvro mapToAvro(TemperatureSensorEvent event);

}