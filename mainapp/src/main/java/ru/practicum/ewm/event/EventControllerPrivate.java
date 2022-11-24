package ru.practicum.ewm.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.ParticipationRequestDto;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users/{userId}/events")
public class EventControllerPrivate {
    private final EventService eventService;

    @Autowired
    public EventControllerPrivate(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventShortDto> findAllUsersEvents(@PathVariable Long userId,
                                                  @RequestParam(required = false, defaultValue = "0") Integer from,
                                                  @RequestParam(required = false, defaultValue = "10") Integer size) {
        return eventService.findAllUsersEvents(userId, from, size);
    }

    @PostMapping
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @RequestBody NewEventDto newEventDto) {
        return eventService.createEvent(userId, newEventDto);
    }

    @PatchMapping
    public EventFullDto patchEvent(@PathVariable Long userId,
                                   @RequestBody UpdateEventRequest updateEventRequest) {
        return eventService.patchEvent(userId, updateEventRequest);
    }

    @GetMapping("/{eventId}")
    public EventFullDto findEventByUserIdAndEventId(@PathVariable Long userId,
                                                    @PathVariable Long eventId) {
        return eventService.findEventByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto cancelEventByUserIdAndEventId(@PathVariable Long userId,
                                                      @PathVariable Long eventId) {
        return eventService.cancelEventByUserIdAndEventId(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> findAllRequestsByUserIdAndEventId(@PathVariable Long userId,
                                                                           @PathVariable Long eventId) {
        return eventService.findAllRequestsByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests/{requestId}/confirm")
    public ParticipationRequestDto confirmAnotherRequestToUsersEvent(@PathVariable Long userId,
                                                                     @PathVariable Long eventId,
                                                                     @PathVariable Long requestId) {
        return eventService.confirmAnotherRequestToUsersEvent(userId, eventId, requestId);
    }

    @PatchMapping("/{eventId}/requests/{requestId}/reject")
    public ParticipationRequestDto rejectAnotherRequestToUsersEvent(@PathVariable Long userId,
                                                                    @PathVariable Long eventId,
                                                                    @PathVariable Long requestId) {
        return eventService.rejectAnotherRequestToUsersEvent(userId, eventId, requestId);
    }
}
