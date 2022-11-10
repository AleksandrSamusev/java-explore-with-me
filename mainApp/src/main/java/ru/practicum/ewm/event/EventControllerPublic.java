package ru.practicum.ewm.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/events")
public class EventControllerPublic {

    private final URI uri = URI.create("${STAT_SERVER_URL} + /hit");
    private final HttpClient client = HttpClient.newHttpClient();
    private final EventServiceImpl eventService;

    @Autowired
    public EventControllerPublic(EventServiceImpl eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventShortDto> getSortedEvents(@RequestParam String text,
                                               @RequestParam List<Integer> categories,
                                               @RequestParam Boolean paid,
                                               @RequestParam String rangeStart,
                                               @RequestParam String rangeEnd,
                                               @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                               @RequestParam String sort,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               HttpServletRequest request) {

        return eventService.getSortedEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort,
                from, size, request);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable Long eventId, HttpServletRequest request) {
        return eventService.getEvent(eventId, request);
    }

}
