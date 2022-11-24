package ru.practicum.ewm.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.practicum.ewm.request.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    List<EventShortDto> findAllUsersEvents(Long userId, Integer from, Integer size);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto patchEvent(Long userId, UpdateEventRequest updateEventRequest);

    EventFullDto findEventByUserIdAndEventId(Long userId, Long eventId);

    EventFullDto cancelEventByUserIdAndEventId(Long userId, Long eventId);

    List<ParticipationRequestDto> findAllRequestsByUserIdAndEventId(Long userId, Long eventId);

    ParticipationRequestDto confirmAnotherRequestToUsersEvent(Long userId, Long eventId, Long requestId);

    ParticipationRequestDto rejectAnotherRequestToUsersEvent(Long userId, Long eventId, Long requestId);

    List<EventFullDto> findAllUsersEventsFull(List<Long> users, List<EventState> states, List<Long> categories,
                                              String rangeStart, String rangeEnd, Integer from, Integer size);

    EventFullDto changeEvent(Long eventId, UpdateEventRequest updateEventRequest);

    EventFullDto publishEvent(Long eventId);

    EventFullDto rejectEvent(Long eventId);

    List<EventShortDto> getSortedEvents(String text, List<Long> categories, Boolean paid,
                                        String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                        String sort, Integer from, Integer size,
                                        HttpServletRequest request);

    EventFullDto getEvent(Long eventId, HttpServletRequest request) throws JsonProcessingException;

    void sentHitStat(HttpServletRequest request);
}
