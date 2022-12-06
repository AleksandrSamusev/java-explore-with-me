package ru.practicum.ewm.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.client.model.EndpointHit;
import ru.practicum.ewm.client.model.ViewStats;
import ru.practicum.ewm.exception.EventNotFoundException;
import ru.practicum.ewm.exception.InvalidParameterException;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.request.*;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;

    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository,
                            RequestRepository requestRepository, CategoryRepository categoryRepository,
                            StatsClient statsClient) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.categoryRepository = categoryRepository;
        this.statsClient = statsClient;
    }

    @Override
    public List<EventShortDto> findAllUsersEvents(Long userId, Integer from, Integer size) {
        validateUserId(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        return EventMapper.toEventShortDtos(eventRepository.findAllEventsByUserId(userId, pageable));
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        validateUserId(userId);
        validateNewEventDto(newEventDto);
        User user = userRepository.getReferenceById(userId);
        Category category = categoryRepository.getReferenceById(newEventDto.getCategory());
        Event event = EventMapper.toEventFromNewEventDto(newEventDto);
        event.setInitiator(user);
        event.setCategory(category);
        event.setAvailable(true);
        event.setRatingFlag(true);
        Event saved = eventRepository.save(event);
        log.info("New event from user with id = {} created", userId);
        return EventMapper.toEventFullDto(saved);
    }

    @Override
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
        log.info("Event with id = {} was patched", updateEventRequest.getEventId());
        return EventMapper.toEventFullDto(eventRepository.save(temp));
    }

    @Override
    public EventFullDto findEventByUserIdAndEventId(Long userId, Long eventId) {
        validateUserId(userId);
        validateEventId(eventId);
        return EventMapper.toEventFullDto(eventRepository.findEventByUserIdAAndEventId(userId, eventId));
    }

    @Override
    public EventFullDto cancelEventByUserIdAndEventId(Long userId, Long eventId) {
        validateUserId(userId);
        validateEventId(eventId);
        Event temp = eventRepository.findEventByUserIdAAndEventId(userId, eventId);
        if (temp.getState().equals(EventState.PENDING)) {
            temp.setState(EventState.CANCELED);
            log.info("Event with id = {} was canceled by user with id = {}", eventId, userId);
            return EventMapper.toEventFullDto(eventRepository.save(temp));
        } else {
            log.info("Only events in pending state can be canceled. Event with id = {} have status - {}",
                    eventId, temp.getState());
            throw new InvalidParameterException("Denied. Wrong state");
        }
    }

    @Override
    public List<ParticipationRequestDto> findAllRequestsByUserIdAndEventId(Long userId, Long eventId) {
        validateUserId(userId);
        validateEventId(eventId);
        if (!Objects.equals(userId, eventRepository.getReferenceById(eventId).getInitiator().getId())) {
            log.info("Only owner can check the requests. User with id = {} not an initiator of event with id = {}",
                    userId, eventId);
            throw new InvalidParameterException("Only owner can check the requests");
        }
        return RequestMapper.toParticipationRequestDtos(requestRepository
                .findAllRequestsByEventId(eventId));
    }

    @Override
    public ParticipationRequestDto confirmAnotherRequestToUsersEvent(Long userId, Long eventId, Long requestId) {
        validateUserId(userId);
        validateEventId(eventId);
        validateRequestId(requestId);

        Request tempRequest = requestRepository.getReferenceById(requestId);
        Event tempEvent = eventRepository.getReferenceById(eventId);

        if (tempEvent.getParticipantLimit() == 0 && !tempEvent.getRequestModeration()) {
            tempRequest.setStatus(RequestStatus.CONFIRMED);
            tempEvent.setConfirmedRequests(tempEvent.getConfirmedRequests() + 1L);
            log.info("Limit - {}. Current requests - {}", tempEvent.getParticipantLimit(),
                    tempEvent.getConfirmedRequests());
            eventRepository.save(tempEvent);

            if (!eventRepository.getReferenceById(eventId).getRatingFlag().equals(Boolean.FALSE)) {
                Event event = eventRepository.getReferenceById(eventId);
                double rating = round(((double) event.getConfirmedRequests()
                        / countUniqueViews(eventId) * 100), 2);
                event.setRating(rating);
                eventRepository.save(event);
            }

        } else if (requestRepository.findAllConfirmedRequestsByEventId(eventId).size()
                == tempEvent.getParticipantLimit() && tempEvent.getParticipantLimit() != 0) {
            log.info("Request with id = {} was not confirmed. Participants limit to event with id = {} reached",
                    requestId, eventId);
            throw new InvalidParameterException("Denied. Participants limit reached");
        } else {
            tempRequest.setStatus(RequestStatus.CONFIRMED);
            tempEvent.setConfirmedRequests(tempEvent.getConfirmedRequests() + 1L);
            eventRepository.save(tempEvent);

            if (!eventRepository.getReferenceById(eventId).getRatingFlag().equals(Boolean.FALSE)) {
                Event event = eventRepository.getReferenceById(eventId);
                double rating = round(((double) event.getConfirmedRequests()
                        / countUniqueViews(eventId) * 100), 2);
                event.setRating(rating);
                eventRepository.save(event);
            }

            if (requestRepository.findAllConfirmedRequestsByEventId(eventId).size()
                    == tempEvent.getParticipantLimit()) {

                Event event = eventRepository.getReferenceById(eventId);
                event.setRatingFlag(Boolean.FALSE);
                eventRepository.save(event);

                log.info("Requests limit ({}) for event - (id - {}) reached. Set all PENDING to REJECT",
                        tempEvent.getParticipantLimit(), eventId);
                List<Request> pendingRequests = requestRepository.findAllPendingRequestsByEventId(eventId);
                for (Request request : pendingRequests) {
                    requestRepository.getReferenceById(request.getId()).setStatus(RequestStatus.REJECTED);
                    log.info("Set request {} to REJECTED", request.getId());
                    requestRepository.save(request);
                }
            }
        }
        log.info("Request with id = {} from user with id = {} to event with id = {} was confirmed",
                requestId, userId, eventId);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(tempRequest));
    }

    @Override
    public ParticipationRequestDto rejectAnotherRequestToUsersEvent(Long userId, Long eventId, Long requestId) {
        validateUserId(userId);
        validateEventId(eventId);
        validateRequestId(requestId);
        Request tempRequest = requestRepository.getReferenceById(requestId);
        tempRequest.setStatus(RequestStatus.REJECTED);
        log.info("Request with id = {} from user with id = {} to event with id = {} was rejected",
                requestId, userId, eventId);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(tempRequest));
    }

    @Override
    public List<EventFullDto> findAllUsersEventsFull(List<Long> users, List<EventState> states, List<Long> categories,
                                                     String rangeStart, String rangeEnd, Integer from, Integer size) {

        LocalDateTime start;
        if (rangeStart.equals("null")) {
            start = LocalDateTime.now();
        } else {
            start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        LocalDateTime end;
        if (rangeEnd.equals("null")) {
            end = LocalDateTime.now().plusYears(100);
        } else {
            end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        log.info("parameters: users - {}, states - {}, categories - {}, rangeStart - {}, rangeEnd - {}," +
                " from - {}, size - {}", users, states, categories, start, end, from, size);
        return EventMapper.toEventFullDtos(eventRepository.findAllUsersEventsFull(users, categories, states,
                start, end, pageable));
    }

    @Override
    public EventFullDto changeEvent(Long eventId, UpdateEventRequest updateEventRequest) {
        Event temp = eventRepository.getReferenceById(eventId);
        if (updateEventRequest.getAnnotation() != null) {
            temp.setAnnotation(updateEventRequest.getAnnotation());
        }
        if (updateEventRequest.getDescription() != null) {
            temp.setDescription(updateEventRequest.getDescription());
        }
        if (updateEventRequest.getEventDate() != null) {
            temp.setEventDate(updateEventRequest.getEventDate());
        }
        if (updateEventRequest.getPaid() != null) {
            temp.setPaid(updateEventRequest.getPaid());
        }
        if (updateEventRequest.getParticipantLimit() != null) {
            temp.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }
        if (updateEventRequest.getTitle() != null) {
            temp.setTitle(updateEventRequest.getTitle());
        }
        if (updateEventRequest.getCategoryId() != null) {
            temp.setCategory(categoryRepository.getReferenceById(updateEventRequest.getCategoryId()));
        }
        log.info("Event with id = {} was updated", eventId);
        return EventMapper.toEventFullDto(eventRepository.save(temp));
    }

    @Override
    public EventFullDto publishEvent(Long eventId) {
        validateEventId(eventId);
        Event tempEvent = eventRepository.getReferenceById(eventId);
        if (tempEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(1L))) {
            log.info("Event with id = {} can't be posted. Less then 1 hour before the event", eventId);
            throw new InvalidParameterException("Denied. Less then 1hr before the event");
        } else if (!tempEvent.getState().equals(EventState.PENDING)) {
            log.info("Event with id = {} can't be posted. Event should have pending status", eventId);
            throw new InvalidParameterException("Denied. Event should have PENDING state");
        }
        tempEvent.setState(EventState.PUBLISHED);
        tempEvent.setPublishedOn(LocalDateTime.now());
        log.info("Event with id = {} was posted", eventId);
        return EventMapper.toEventFullDto(eventRepository.save(tempEvent));
    }

    @Override
    public EventFullDto rejectEvent(Long eventId) {
        validateEventId(eventId);
        Event tempEvent = eventRepository.getReferenceById(eventId);
        if (tempEvent.getState().equals(EventState.PUBLISHED)) {
            log.info("Event (id - {}) is already published", eventId);
            throw new InvalidParameterException("Denied. Event is already published");
        }
        tempEvent.setState(EventState.CANCELED);
        log.info("Event with id = {} canceled", eventId);
        return EventMapper.toEventFullDto(eventRepository.save(tempEvent));
    }

    @Override
    public List<EventShortDto> getSortedEvents(String text, List<Long> categories, Boolean paid,
                                               String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                               String sort, Integer from, Integer size,
                                               HttpServletRequest request) {

        String sorting = "id";
        Pageable pageable;
        if (sort != null) {
            if (sort.equals(EventSortType.EVENT_DATE.toString())) {
                sorting = "eventDate";
            } else if (sort.equals(EventSortType.VIEWS.toString())) {
                sorting = "views";
            } else if (sort.equals(EventSortType.RATING.toString())) {
                sorting = "rating";
            }
        }
        LocalDateTime start;
        if (rangeStart == null) {
            start = LocalDateTime.now().minusYears(100);
        } else {
            start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        LocalDateTime end;
        if (rangeEnd == null) {
            end = LocalDateTime.now().plusYears(100);
        } else {
            end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        if (!sorting.equals("rating")) {
            pageable = PageRequest.of(from / size, size, Sort.by(sorting));
        } else {
            pageable = PageRequest.of(from / size, size, Sort.by(sorting).descending());
        }
        log.info("parameters: text - {}, categories - {}, paid - {}, rangeStart - {}, rangeEnd - {}, " +
                        "onlyAvailable - {}, sort - {}, from - {}, size - {}", text, categories, paid, start, end,
                onlyAvailable, sort, from, size);

        List<Event> sortedEvents = eventRepository.getFilteredEvents(text, categories,
                paid, start, end, pageable);

        if (onlyAvailable) {
            for (Event event : sortedEvents) {
                if (event.getConfirmedRequests() == event.getParticipantLimit()) {
                    sortedEvents.remove(event);
                }
            }
        }
        sortedEvents.forEach(e -> {
            LocalDateTime statStart = LocalDateTime.now().minusYears(100);
            LocalDateTime statsEnd = LocalDateTime.now();
            List<ViewStats> stats;
            try {
                stats = statsClient.getStats(statStart,
                        statsEnd,
                        List.of(String.format("/events/%s", e.getId())),
                        false);
                log.info("set views - {}", stats.size());
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
            if (stats.isEmpty()) {
                e.setViews(0);
                log.info("set views - 0");
            } else {
                e.setViews(stats.get(0).getHits());
                log.info("set views - {}", stats.get(0).getHits());
            }
        });
        eventRepository.saveAll(sortedEvents);

        sentHitStat(request);

        return EventMapper.toEventShortDtos(sortedEvents);
    }

    @Override
    public EventFullDto getEvent(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException("Event not found"));
        String uri = request.getRequestURI();

        event.setViews(countNotUniqueViews(eventId, uri));
        eventRepository.save(event);
        sentHitStat(request);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public void sentHitStat(HttpServletRequest request) {
        log.info("request URL {}", request.getRequestURI());
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp("mainApp");
        endpointHit.setUri(request.getRequestURI());
        endpointHit.setIp(request.getRemoteAddr());
        endpointHit.setTimestamp(LocalDateTime.now());
        statsClient.saveStats(endpointHit);
    }

    private void validateEventId(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            log.info("Event with id = {} not found", eventId);
            throw new EventNotFoundException("Event not found");
        }
    }

    private void validateRequestId(Long requestId) {
        if (!requestRepository.existsById(requestId)) {
            log.info("Request with id = {} not found", requestId);
            throw new EventNotFoundException("Event not found");
        }
    }

    private void validateUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.info("User with id = {} not found", userId);
            throw new UserNotFoundException("User not found");
        }
    }

    private void validateNewEventDto(NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
            log.info("Less then 2hrs before the event");
            throw new InvalidParameterException("Denied. Less then 2hrs before the event.");
        }
        if (newEventDto.getAnnotation() == null || newEventDto.getAnnotation().isBlank() ||
                newEventDto.getAnnotation().length() > 2000 || newEventDto.getAnnotation().length() < 20) {
            log.info("Annotation should not be > 2000 or < 20 chars");
            throw new InvalidParameterException("annotation does not meet the requirements");
        }
        if (newEventDto.getCategory() == null) {
            log.info("Category is null");
            throw new InvalidParameterException("categories does not meet the requirements");
        }
        if (newEventDto.getDescription() == null || newEventDto.getDescription().isBlank() ||
                newEventDto.getDescription().length() > 7000 || newEventDto.getDescription().length() < 20) {
            log.info("Description should not be > then 7000 or less then 20");
            throw new InvalidParameterException("description does not meet the requirements");
        }
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now())) {
            log.info("The date is in past");
            throw new InvalidParameterException("event date does not meet the requirements");
        }
        if (newEventDto.getLocation() == null || newEventDto.getLocation().getLat() == null ||
                newEventDto.getLocation().getLon() == null) {
            log.info("Location parameters is null");
            throw new InvalidParameterException("location does not meet the requirements");
        }
        if (newEventDto.getTitle() == null || newEventDto.getTitle().isBlank()
                || newEventDto.getTitle().length() > 120 || newEventDto.getTitle().length() < 3) {
            log.info("Title should be more then 3 chars and less then 120");
            throw new InvalidParameterException("title does not meet the requirements");
        }
    }

    private int countNotUniqueViews(Long eventId, String uri) {
        List<ViewStats> stats;
        int views;
        try {
            stats = statsClient.getStats(
                    LocalDateTime.now().minusYears(100),
                    LocalDateTime.now(),
                    List.of(uri),
                    false);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
        if (stats.isEmpty()) {
            views = 0;
        } else {
            views = stats.get(0).getHits();
        }
        log.info("VIEWS = {}", views);
        return views;
    }

    private int countUniqueViews(Long eventId) {
        List<ViewStats> stats;
        int views;
        try {
            stats = statsClient.getStats(
                    eventRepository.getReferenceById(eventId).getPublishedOn(),
                    LocalDateTime.now(),
                    List.of("/events/" + eventId),
                    true);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
        if (stats.isEmpty()) {
            views = 0;
        } else {
            views = stats.size();
        }
        log.info("VIEWS = {}", views);
        return views;
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
