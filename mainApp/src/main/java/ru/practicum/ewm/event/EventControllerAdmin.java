package ru.practicum.ewm.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin/events")
public class EventControllerAdmin {
    private final EventServiceImpl eventService;

    @Autowired
    public EventControllerAdmin(EventServiceImpl eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventFullDto> findAllUsersEventsFull(@RequestParam List<Long> ids,
                                                     @RequestParam List<String> states,
                                                     @RequestParam List<String> categories,
                                                     @RequestParam String rangeStart,
                                                     @RequestParam String rangeEnd,
                                                     @RequestParam(required = false, defaultValue = "0") Integer from,
                                                     @RequestParam(required = false, defaultValue = "10") Integer size) {
        return eventService.findAllUsersEventsFull(ids, states, categories, rangeStart, rangeEnd, from, size);

    }
}
