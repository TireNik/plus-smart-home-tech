package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import ru.yandex.practicum.service.EventService;
import ru.yandex.practicum.model.HubEvent;
import ru.yandex.practicum.model.SensorEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/events")
public class EventController {

    private final Map<String, EventService> eventServices;

    public EventController(Map<String, EventService> eventServices) {
        this.eventServices = eventServices;
    }

    @PostMapping("/sensors")
    public ResponseEntity<Void> collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        EventService service = eventServices.get("sensorEventService");
        service.processEvent(event);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/hubs")
    public ResponseEntity<Void> collectHubEvent(@Valid @RequestBody HubEvent event) {
        EventService service = eventServices.get("hubEventService");
        service.processEvent(event);
        return ResponseEntity.ok().build();
    }
}