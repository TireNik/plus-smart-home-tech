package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.service.EventService;
import ru.yandex.practicum.model.HubEvent;
import ru.yandex.practicum.model.SensorEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService service;

    @PostMapping("/hubs")
    public void sendHubEvent(@Valid @RequestBody HubEvent ev) {
        service.processEvent(ev);
    }

    @PostMapping("/sensors")
    public void sendSensorEvent(@Valid @RequestBody SensorEvent ev) {
        service.processEvent(ev);
    }
}