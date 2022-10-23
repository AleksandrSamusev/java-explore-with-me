package ru.practicum.ewm.event;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.exception.EventNotFoundException;
import ru.practicum.ewm.exception.InvalidParameterException;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.request.*;
import ru.practicum.ewm.user.UserMapper;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository, RequestRepository requestRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }

    public List<EventShortDto> findAllUsersEvents(Long userId, Integer from, Integer size) {
        validateUserId(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("eventId"));
        return EventMapper.toEventShortDtos(eventRepository.findAllEventsByUserId(userId, pageable));
    }

    public EventFullDto createEvent(Long userId, EventFullDto eventFullDto) {
        validateUserId(userId);
        validateEventFullDto(eventFullDto);
        return EventMapper.toEventFullDto(eventRepository.save(EventMapper.toEvent(eventFullDto)));
    }

    public EventFullDto patchEvent(Long userId, EventFullDto eventFullDto) {
        if (!eventFullDto.getState().equals(EventState.PUBLISHED)) {
            validateUserId(userId);
            validateEventFullDto(eventFullDto);
            Event temp = eventRepository.findById(eventFullDto.getEventId())
                    .orElseThrow(() -> new EventNotFoundException("Event not found"));
            if (eventFullDto.getEventDate() != null) {
                temp.setEventDate(eventFullDto.getEventDate());
            }
            if (eventFullDto.getPaid() != null) {
                temp.setPaid(eventFullDto.getPaid());
            }
            if (eventFullDto.getCategoryDto() != null) {
                temp.setCategory(CategoryMapper.toCategory(eventFullDto.getCategoryDto()));
            }
            if (eventFullDto.getAnnotation() != null) {
                temp.setAnnotation(eventFullDto.getAnnotation());
            }
            if (eventFullDto.getConfirmedRequests() != null) {
                temp.setConfirmedRequests(eventFullDto.getConfirmedRequests());
            }
            if (eventFullDto.getDescription() != null) {
                temp.setDescription(eventFullDto.getDescription());
            }
            temp.setRequestModeration(eventFullDto.getRequestModeration());
            temp.setCreatedOn(LocalDateTime.now());
            if (eventFullDto.getInitiator() != null) {
                temp.setInitiator(UserMapper.toUserFromShortDto(eventFullDto.getInitiator()));
            }
            if (eventFullDto.getLocation() != null) {
                temp.setLocation(eventFullDto.getLocation());
            }
            if (eventFullDto.getParticipantLimit() != null) {
                temp.setParticipantLimit(eventFullDto.getParticipantLimit());
            }
            if (eventFullDto.getPublishedOn() != null) {
                temp.setPublishedOn(eventFullDto.getPublishedOn());
            }
            if (eventFullDto.getTitle() != null) {
                temp.setTitle(eventFullDto.getTitle());
            }
            if (eventFullDto.getViews() != null) {
                temp.setViews(eventFullDto.getViews());
            }
            if (eventFullDto.getState().equals(EventState.CANCELLED)) {
                temp.setState(EventState.PENDING);
            } else {
                temp.setState(eventFullDto.getState());
            }
            return EventMapper.toEventFullDto(eventRepository.save(temp));
        } else {
            throw new InvalidParameterException("Denied. Event is already published.");
        }
    }

    public EventFullDto findEventByUserIdAndEventId(Long userId, Long eventId) {
        validateUserId(userId);
        validateEventId(eventId);
        return EventMapper.toEventFullDto(eventRepository.findEventByUserIdAAndEventId(userId, eventId));
    }

    public EventFullDto cancelEventByUserIdAndEventId(Long userId, Long eventId) {
        validateUserId(userId);
        validateEventId(eventId);
        EventFullDto temp = EventMapper.toEventFullDto(eventRepository.findEventByUserIdAAndEventId(userId, eventId));
        if (temp.getState().equals(EventState.PENDING)) {
            temp.setState(EventState.CANCELLED);
            return temp;
        } else {
            throw new InvalidParameterException("Denied. Wrong state");
        }
    }

    public List<ParticipationRequestDto> findAllRequestsByUserIdAndEventId(Long userId, Long eventId) {
        validateUserId(userId);
        validateEventId(eventId);
        return RequestMapper.toParticipationRequestDtos(requestRepository
                .findAllRequestsByUserIdAndEventId(userId, eventId));
    }

    public ParticipationRequestDto confirmAnotherRequestToUsersEvent(Long userId, Long eventId, Long requestId) {
        validateUserId(userId);
        validateEventId(eventId);
        validateRequestId(requestId);
        Request tempRequest = requestRepository.getReferenceById(requestId);
        Event tempEvent = eventRepository.getReferenceById(eventId);
        if (tempEvent.getParticipantLimit() == 0 || !tempEvent.getRequestModeration()) {
            tempRequest.setStatus(RequestStatus.CONFIRMED);
        } else if (requestRepository.findAllConfirmedRequestsByEventId(eventId).size()
                == tempEvent.getParticipantLimit()) {
            throw new InvalidParameterException("Denied. Participants limit reached");
        } else if (requestRepository.findAllConfirmedRequestsByEventId(eventId).size()
                == tempEvent.getParticipantLimit() - 1) {
            tempRequest.setStatus(RequestStatus.CONFIRMED);
            List<Request> pendingRequests = requestRepository.findAllPendingRequestsByEventId(eventId);
            for (Request request : pendingRequests) {
                requestRepository.getReferenceById(request.getRequestId()).setStatus(RequestStatus.CANCELLED);
            }
        }
        return RequestMapper.toParticipationRequestDto(requestRepository.save(tempRequest));
    }

    public ParticipationRequestDto rejectAnotherRequestToUsersEvent(Long userId, Long eventId, Long requestId) {
        validateUserId(userId);
        validateEventId(eventId);
        validateRequestId(requestId);
        Request tempRequest = requestRepository.getReferenceById(requestId);
        tempRequest.setStatus(RequestStatus.CANCELLED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(tempRequest));
    }

    public List<EventFullDto> findAllUsersEventsFull(List<Long> ids, List<String> states, List<String> categories,
                                                     String rangeStart, String rangeEnd, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("eventId"));
        return EventMapper.toEventFullDtos(eventRepository.findAllUsersEventsFull(ids, categories, states,
                rangeStart, rangeEnd, pageable));
    }

    public EventFullDto changeEvent(Long eventId, EventFullDto eventFullDto) {
        Event temp = eventRepository.getReferenceById(eventId);
        temp.setAnnotation(eventFullDto.getAnnotation());
        temp.setCategory(CategoryMapper.toCategory(eventFullDto.getCategoryDto()));
        temp.setDescription(eventFullDto.getDescription());
        temp.setEventDate(eventFullDto.getEventDate());
        temp.setLocation(eventFullDto.getLocation());
        temp.setPaid(eventFullDto.getPaid());
        temp.setParticipantLimit(eventFullDto.getParticipantLimit());
        temp.setRequestModeration(eventFullDto.getRequestModeration());
        temp.setTitle(eventFullDto.getTitle());
        return EventMapper.toEventFullDto(eventRepository.save(temp));
    }

    public EventFullDto publishEvent(Long eventId) {
        validateEventId(eventId);
        Event tempEvent = eventRepository.getReferenceById(eventId);
        if (tempEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(1L))) {
            throw new InvalidParameterException("Denied. Less then 1hr before the event");
        } else if (!tempEvent.getState().equals(EventState.PENDING)) {
            throw new InvalidParameterException("Denied. Event should have PENDING state");
        }
        tempEvent.setState(EventState.PUBLISHED);
        return EventMapper.toEventFullDto(eventRepository.save(tempEvent));
    }

    public EventFullDto rejectEvent(Long eventId) {
        validateEventId(eventId);
        Event tempEvent = eventRepository.getReferenceById(eventId);
        if (tempEvent.getState().equals(EventState.PUBLISHED)) {
            throw new InvalidParameterException("Denied. Event is already published");
        }
        tempEvent.setState(EventState.CANCELLED);
        return EventMapper.toEventFullDto(eventRepository.save(tempEvent));
    }

    private void validateEventId(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Event not found");
        }
    }

    private void validateRequestId(Long requestId) {
        if (!requestRepository.existsById(requestId)) {
            throw new EventNotFoundException("Event not found");
        }
    }

    private void validateUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }
    }

    private void validateEventFullDto(EventFullDto eventFullDto) {
        if (eventFullDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new InvalidParameterException("Denied. Less then 2hrs before the event.");
        }
    }

    private void validateIds(List<Long> ids) {
        for (Long id : ids) {
            if (!userRepository.existsById(id)) {
                throw new UserNotFoundException("User not found");
            }
        }
    }
}
