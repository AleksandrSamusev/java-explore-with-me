package ru.practicum.ewm.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventControllerPrivate {
    private final EventServiceImpl eventService;

    @Autowired
    public EventControllerPrivate(EventServiceImpl eventService) {
        this.eventService = eventService;
    }
}
