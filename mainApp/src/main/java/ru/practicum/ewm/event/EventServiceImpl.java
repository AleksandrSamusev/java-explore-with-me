package ru.practicum.ewm.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.exception.EventNotFoundException;
import ru.practicum.ewm.exception.InvalidParameterException;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.request.*;
import ru.practicum.ewm.user.User;
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
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;

    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository,
                            RequestRepository requestRepository, CategoryRepository categoryRepository, StatsClient statsClient) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.categoryRepository = categoryRepository;
        this.statsClient = statsClient;
    }

    public List<EventShortDto> findAllUsersEvents(Long userId, Integer from, Integer size) {
        validateUserId(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        return EventMapper.toEventShortDtos(eventRepository.findAllEventsByUserId(userId, pageable));
    }

    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        User user = userRepository.getReferenceById(userId);
        Category category = categoryRepository.getReferenceById(newEventDto.getCategory());
        Event event = EventMapper.toEventFromNewEventDto(newEventDto);
        event.setInitiator(user);
        event.setCategory(category);
        event.setAvailable(true);
        Event saved = eventRepository.save(event);
        return EventMapper.toEventFullDto(saved);
    }

    public EventFullDto patchEvent(Long userId, UpdateEventRequest updateEventRequest) {
        validateUserId(userId);
        Event temp = eventRepository.findById(updateEventRequest.getEventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        if (updateEventRequest.getEventDate() != null) {
            temp.setEventDate(updateEventRequest.getEventDate());
        }
        if (updateEventRequest.getPaid() != null) {
            temp.setPaid(updateEventRequest.getPaid());
        }
        if (updateEventRequest.getCategoryId() != null) {
            temp.setCategory(categoryRepository.getReferenceById(updateEventRequest.getCategoryId()));
        }
        if (updateEventRequest.getAnnotation() != null) {
            temp.setAnnotation(updateEventRequest.getAnnotation());
        }
        if (updateEventRequest.getDescription() != null) {
            temp.setDescription(updateEventRequest.getDescription());
        }

        if (updateEventRequest.getParticipantLimit() != null) {
            temp.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }

        if (updateEventRequest.getTitle() != null) {
            temp.setTitle(updateEventRequest.getTitle());
        }
        return EventMapper.toEventFullDto(eventRepository.save(temp));
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

    public List<EventFullDto> findAllUsersEventsFull(List<Long> users, List<EventState> states, List<Long> categories,
                                                     String rangeStart, String rangeEnd, Integer from, Integer size) {
        LocalDateTime start;
        LocalDateTime end;

        if (rangeStart != null && rangeEnd != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            start = LocalDateTime.parse(rangeStart, formatter);
            end = LocalDateTime.parse(rangeEnd, formatter);
        } else {
            start = LocalDateTime.now();
            end = LocalDateTime.now().plusYears(100);
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        return EventMapper.toEventFullDtos(eventRepository.findAllUsersEventsFull(users, categories, states,
                start, end, pageable));
    }

    public EventFullDto changeEvent(Long eventId, EventFullDto eventFullDto) {
        Event temp = eventRepository.getReferenceById(eventId);
        temp.setAnnotation(eventFullDto.getAnnotation());
        temp.setCategory(CategoryMapper.toCategory(eventFullDto.getCategory()));
        temp.setDescription(eventFullDto.getDescription());
        temp.setEventDate(eventFullDto.getEventDate());
        temp.setLat(eventFullDto.getLocation().getLat());
        temp.setLon(eventFullDto.getLocation().getLon());
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

    private void validateUpdateEventRequest(UpdateEventRequest updateEventRequest) {
        if (!eventRepository.existsById(updateEventRequest.getEventId())) {
            throw new EventNotFoundException("Event not found");
        }
    }

    private void validateUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }
    }

    private void validateNewEventDto(NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
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
                paid, start, end, pageable);

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
