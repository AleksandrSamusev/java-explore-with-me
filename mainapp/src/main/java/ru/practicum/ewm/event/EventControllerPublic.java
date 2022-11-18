package ru.practicum.ewm.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/events")
public class EventControllerPublic {

    private final EventServiceImpl eventService;

    @Autowired
    public EventControllerPublic(EventServiceImpl eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventShortDto> getSortedEvents(@RequestParam String text,
                                               @RequestParam List<Long> categories,
                                               @RequestParam Boolean paid,
                                               @RequestParam(defaultValue = "null") String rangeStart,
                                               @RequestParam(defaultValue = "null") String rangeEnd,
                                               @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                               @RequestParam(defaultValue = "id") String sort,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               HttpServletRequest request) {

        eventService.sentHitStat(request);
        return eventService.getSortedEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort,
                from, size, request);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable Long eventId, HttpServletRequest request) throws JsonProcessingException {
        eventService.sentHitStat(request);
        return eventService.getEvent(eventId, request);
    }

}
