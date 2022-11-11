package ru.practicum.ewm.event;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.location.Location;
import ru.practicum.ewm.user.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class EventMapper {
    public static Event toEvent(EventFullDto eventFullDto) {
        Event event = new Event();
        event.setId(eventFullDto.getEventId());
        event.setEventDate(eventFullDto.getEventDate());
        event.setAnnotation(eventFullDto.getAnnotation());
        event.setCategory(CategoryMapper.toCategory(eventFullDto.getCategoryDto()));
        event.setPaid(eventFullDto.getPaid());
        event.setDescription(eventFullDto.getDescription());
        event.setConfirmedRequests(eventFullDto.getConfirmedRequests());
        event.setCreatedOn(eventFullDto.getCreatedOn());
        event.setInitiator(UserMapper.toUserFromShortDto(eventFullDto.getInitiator()));
        event.setLat(eventFullDto.getLocation().getLat());
        event.setLon(eventFullDto.getLocation().getLon());
        event.setParticipantLimit(eventFullDto.getParticipantLimit());
        event.setPublishedOn(eventFullDto.getPublishedOn());
        event.setState(eventFullDto.getState());
        event.setTitle(eventFullDto.getTitle());
        event.setViews(eventFullDto.getViews());
        event.setRequestModeration(eventFullDto.getRequestModeration());
        return event;
    }

    public static EventFullDto toEventFullDto(Event event) {
        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setEventId(event.getId());
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategoryDto(CategoryMapper.toCategoryDto(event.getCategory()));
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setConfirmedRequests(event.getConfirmedRequests());
        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        Location location = new Location();
        location.setLat(event.getLat());
        location.setLon(event.getLon());
        eventFullDto.setLocation(location);
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setPublishedOn(event.getPublishedOn());
        eventFullDto.setState(event.getState());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setViews(event.getViews());
        eventFullDto.setRequestModeration(event.getRequestModeration());
        return eventFullDto;
    }

    public static EventShortDto toEventShortDto(Event event) {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        eventShortDto.setId(event.getId());
        eventShortDto.setEventDate(event.getEventDate());
        eventShortDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setConfirmedRequests(event.getConfirmedRequests());
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setViews(event.getViews());
        return eventShortDto;
    }

    public static Event toEventFromNewEventDto(NewEventDto newEventDto) {
        Event event = new Event();
        event.setCreatedOn(LocalDateTime.now());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setEventDate(newEventDto.getEventDate());
        event.setLon(newEventDto.getLocation().getLon());
        event.setLat(newEventDto.getLocation().getLat());
        event.setPaid(newEventDto.getPaid());
        event.setTitle(newEventDto.getTitle());
        event.setDescription(newEventDto.getDescription());
        event.setAnnotation(newEventDto.getAnnotation());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.getRequestModeration());
        event.setState(EventState.PENDING);
        return event;
    }

    public static List<EventShortDto> toEventShortDtos(List<Event> events) {
        List<EventShortDto> dtos = new ArrayList<>();
        for (Event event : events) {
            dtos.add(toEventShortDto(event));
        }
        return dtos;
    }

    public static List<EventFullDto> toEventFullDtos(List<Event> events) {
        List<EventFullDto> dtos = new ArrayList<>();
        for (Event event : events) {
            dtos.add(toEventFullDto(event));
        }
        return dtos;
    }
}
