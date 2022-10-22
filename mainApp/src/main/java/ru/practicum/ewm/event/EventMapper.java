package ru.practicum.ewm.event;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.user.UserMapper;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventMapper {
    public static Event toEvent(EventFullDto eventFullDto) {
        Event event = new Event();
        event.setEventId(eventFullDto.getEventId());
        event.setEventDate(eventFullDto.getEventDate());
        event.setAnnotation(eventFullDto.getAnnotation());
        event.setCategory(CategoryMapper.toCategory(eventFullDto.getCategoryDto()));
        event.setPaid(eventFullDto.getPaid());
        event.setDescription(eventFullDto.getDescription());
        event.setConfirmedRequests(eventFullDto.getConfirmedRequests());
        event.setCreatedOn(eventFullDto.getCreatedOn());
        event.setInitiator(UserMapper.toUserFromShortDto(eventFullDto.getInitiator()));
        event.setLocation(eventFullDto.getLocation());
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
        eventFullDto.setEventId(event.getEventId());
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategoryDto(CategoryMapper.toCategoryDto(event.getCategory()));
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setConfirmedRequests(event.getConfirmedRequests());
        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        eventFullDto.setLocation(event.getLocation());
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
        eventShortDto.setCategoryDto(CategoryMapper.toCategoryDto(event.getCategory()));
        eventShortDto.setEventId(event.getEventId());
        eventShortDto.setEventDate(event.getEventDate());
        eventShortDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setConfirmedRequests(event.getConfirmedRequests());
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setViews(event.getViews());
        return eventShortDto;
    }

    public static List<EventShortDto> toEventShortDtos(List<Event> events) {
        List<EventShortDto> dtos = new ArrayList<>();
        for (Event event : events) {
            dtos.add(toEventShortDto(event));
        }
        return dtos;
    }
}
