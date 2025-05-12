package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.EventService;
import ru.yandex.practicum.model.HubEvent;
import ru.yandex.practicum.model.SensorEvent;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/events")
@Slf4j
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