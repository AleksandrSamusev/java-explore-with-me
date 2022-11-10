package ru.practicum.ewm.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.exception.EventNotFoundException;
import ru.practicum.ewm.exception.InvalidParameterException;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.request.*;
import ru.practicum.ewm.user.UserMapper;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.stat.model.EndpointHit;
import ru.practicum.stat.model.ViewStats;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository, RequestRepository requestRepository, StatsClient client, StatsClient statsClient) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.statsClient = statsClient;
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
                requestRepository.getReferenceById(request.getId()).setStatus(RequestStatus.CANCELLED);
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

    public List<EventShortDto> getSortedEvents(String text, List<Integer> categories, Boolean paid,
                                               String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                               String sort, Integer from, Integer size, HttpServletRequest request) {
        LocalDateTime start;
        LocalDateTime end;
        String sorting = "";
        String availableCondition;

        if (rangeStart != null && rangeEnd != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            start = LocalDateTime.parse(rangeStart, formatter);
            end = LocalDateTime.parse(rangeEnd, formatter);
        } else {
            start = LocalDateTime.now();
            end = LocalDateTime.now().plusYears(100);
        }
        if (sort.equals(EventSortType.EVENT_DATE.toString())) {
            sorting = "eventDate";
        } else if (sort.equals(EventSortType.VIEWS.toString())) {
            sorting = "views";
        }
        if (onlyAvailable.equals(false)) {
            availableCondition = "";
        } else {
            availableCondition = "e.confirmedRequests < e.participantLimit";
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(sorting));

        List<Event> sortedEvents = eventRepository.getFilteredEvents(text, categories,
                paid, start, end, availableCondition, pageable);

        sortedEvents.forEach(e -> {
            List<ViewStats> stats = statsClient.stats(start,
                    end,
                    List.of(String.format("/events/%s", e.getId())),
                    false);
            if (stats.isEmpty()) {
                e.setViews(0);
            } else {
                e.setViews(stats.get(0).getHits());
            }
        });
        saveStatistics(request);
        return EventMapper.toEventShortDtos(sortedEvents);

    }

    @Override
    public EventFullDto getEvent(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException("Event not found"));

        List<String> uris = List.of(request.getRequestURI());
        List<ViewStats> stats = statsClient.stats(LocalDateTime.now(), LocalDateTime.now(), uris, true);
        int hits = stats.stream()
                .filter(s -> s.getApp().equals("mainApp"))
                .map(ViewStats::getHits).mapToInt(Integer::intValue).sum();
        event.setViews(hits);
        saveStatistics(request);
        return EventMapper.toEventFullDto(event);
    }

    private void saveStatistics(HttpServletRequest request) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp("mainApp");
        endpointHit.setUri(request.getRemoteAddr());
        endpointHit.setIp(request.getRequestURI());
        endpointHit.setTimestamp(String.valueOf(LocalDateTime.now()));
        statsClient.hit(endpointHit);
    }
}
