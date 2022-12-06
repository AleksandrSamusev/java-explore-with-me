package ru.practicum.ewm.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin/events")
public class EventControllerAdmin {
    private final EventService eventService;
    private final DataLoader dataLoader;

    @Autowired
    public EventControllerAdmin(EventService eventService, DataLoader dataLoader) {
        this.eventService = eventService;
        this.dataLoader = dataLoader;
    }

    @GetMapping
    public List<EventFullDto> findAllUsersEventsFull(@RequestParam(required = false) List<Long> users,
                                                     @RequestParam(required = false, defaultValue = "PUBLISHED," +
                                                             " PENDING, CANCELED") List<EventState> states,
                                                     @RequestParam(required = false) List<Long> categories,
                                                     @RequestParam(required = false, defaultValue = "null") String rangeStart,
                                                     @RequestParam(required = false, defaultValue = "null") String rangeEnd,
                                                     @RequestParam(required = false, defaultValue = "0") Integer from,
                                                     @RequestParam(required = false, defaultValue = "10") Integer size) {
        return eventService.findAllUsersEventsFull(users, states, categories, rangeStart, rangeEnd, from, size);

    }

    @PutMapping("/{eventId}")
    public EventFullDto changeEvent(@PathVariable Long eventId,
                                    @RequestBody UpdateEventRequest updateEventRequest) {
        return eventService.changeEvent(eventId, updateEventRequest);
    }

    @PatchMapping("/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable Long eventId) {
        return eventService.publishEvent(eventId);
    }

    @PatchMapping("/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable Long eventId) {
        return eventService.rejectEvent(eventId);
    }

    @PostMapping("/loadData")
    public void loadData() {
        dataLoader.loadData();
    }
}
