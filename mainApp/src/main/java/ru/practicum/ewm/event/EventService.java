package ru.practicum.ewm.event;

import javax.servlet.http.HttpServletRequest;

public interface EventService {

    EventFullDto getEvent(Long eventId, HttpServletRequest request);
}
