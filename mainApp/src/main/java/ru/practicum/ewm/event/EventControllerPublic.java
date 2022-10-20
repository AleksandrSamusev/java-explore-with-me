package ru.practicum.ewm.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventControllerPublic {
    private final EventServiceImpl eventService;

    @Autowired
    public EventControllerPublic(EventServiceImpl eventService) {
        this.eventService = eventService;
    }
}
