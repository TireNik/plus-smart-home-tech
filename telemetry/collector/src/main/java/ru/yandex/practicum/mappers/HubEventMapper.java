package ru.yandex.practicum.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassMapping;
import ru.yandex.practicum.dto.hub.DeviceAddedEvent;
import ru.yandex.practicum.dto.hub.DeviceRemovedEvent;
import ru.yandex.practicum.dto.hub.ScenarioAddedEvent;
import ru.yandex.practicum.dto.hub.ScenarioRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.HubEvent;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface HubEventMapper {

    @Mapping(target = "payload", source = ".", qualifiedByName = "mapClass")
    HubEventAvro mapToAvro(HubEvent event);

    @Named("mapClass")
    @SubclassMapping(source = DeviceAddedEvent.class, target = DeviceAddedEventAvro.class)
    @SubclassMapping(source = DeviceRemovedEvent.class, target = DeviceRemovedEventAvro.class)
    @SubclassMapping(source = ScenarioAddedEvent.class, target = ScenarioAddedEventAvro.class)
    @SubclassMapping(source = ScenarioRemovedEvent.class, target = ScenarioRemovedEventAvro.class)
    Object mapClass(HubEvent event);

    @Mapping(target = "type", source = "deviceType")
    DeviceAddedEventAvro mapToAvro(DeviceAddedEvent event);

    DeviceRemovedEventAvro mapToAvro(DeviceRemovedEvent event);

    ScenarioAddedEventAvro mapToAvro(ScenarioAddedEvent event);

    ScenarioRemovedEventAvro mapToAvro(ScenarioRemovedEvent event);

    default long map(Instant value) {
        return value.toEpochMilli();
    }
}
