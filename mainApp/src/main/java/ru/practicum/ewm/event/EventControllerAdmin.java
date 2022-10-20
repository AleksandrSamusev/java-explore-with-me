package ru.practicum.ewm.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventControllerAdmin {
    private final EventServiceImpl eventService;

    @Autowired
    public EventControllerAdmin(EventServiceImpl eventService) {
        this.eventService = eventService;
    }
}
