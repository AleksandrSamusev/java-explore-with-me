package ru.practicum.ewm.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.exception.EventNotFoundException;
import ru.practicum.ewm.exception.InvalidParameterException;
import ru.practicum.ewm.exception.RequestNotFoundException;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public RequestServiceImpl(RequestRepository requestRepository, UserRepository userRepository,
                              EventRepository eventRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public List<ParticipationRequestDto> findAllUsersRequests(Long userId) {
        validateUserId(userId);
        return RequestMapper.toParticipationRequestDtos(requestRepository.findAllRequestsByUserId(userId));
    }

    @Override
    public ParticipationRequestDto createRequestFromCurrentUser(Long userId, Long eventId) {
        validateUserId(userId);
        validateEventId(eventId);
        List<Request> usersRequests = requestRepository.findAllRequestsByUserId(userId);
        for (Request request : usersRequests) {
            if (request.getEventId().equals(eventId) && (request.getStatus().equals(RequestStatus.PENDING) ||
                    (request.getStatus().equals(RequestStatus.CONFIRMED)))) {
                log.info("Request from user with id = {} to event with id = {} already exists", userId, eventId);
                throw new InvalidParameterException("Denied. Request already created");
            }
        }
        if (eventRepository.getReferenceById(eventId).getInitiator().getId().equals(userId)) {
            log.info("Request was not created. User with id = {} is an initiator of the event with id = {}",
                    userId, eventId);
            throw new InvalidParameterException("Denied. Can't be requested by initiator of the event");
        } else if (!eventRepository.getReferenceById(eventId).getState().equals(EventState.PUBLISHED)) {
            log.info("Request from user with id = {} was not created. Event with id = {} not published yet",
                    userId, eventId);
            throw new InvalidParameterException("Denied. Event is not published yet");
        } else if (eventRepository.getReferenceById(eventId).getRequestModeration() &&
                (requestRepository.findAllConfirmedRequestsByEventId(eventId).size() ==
                        eventRepository.getReferenceById(eventId).getParticipantLimit() &&
                        eventRepository.getReferenceById(eventId).getParticipantLimit() != 0)) {
            log.info("Request from user with id = {} was not created. Participants limit to event with id = {} reached",
                    userId, eventId);
            throw new InvalidParameterException("Denied. Participants limit reached.");
        } else {

            Request request = new Request();
            request.setRequesterId(userId);
            request.setCreated(LocalDateTime.now());
            request.setEventId(eventId);
            if (eventRepository.getReferenceById(eventId).getRequestModeration().equals(Boolean.TRUE)) {
                request.setStatus(RequestStatus.PENDING);
            } else {
                request.setStatus(RequestStatus.CONFIRMED);
                Event eventForSave = eventRepository.getReferenceById(eventId);
                eventForSave.setConfirmedRequests(eventForSave.getConfirmedRequests() + 1);
                eventRepository.save(eventForSave);
            }
            log.info("Request from user with id = {} to event with id = {} was created", userId, eventId);
            return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
        }
    }

    @Override
    public ParticipationRequestDto cancelOwnRequest(Long userId, Long requestId) {
        validateUserId(userId);
        validateRequestId(requestId);
        Request temp = requestRepository.getReferenceById(requestId);
        if (temp.getStatus().equals(RequestStatus.CANCELED)) {
            log.info("Request with id = {} from user with id = {} already cancelled", requestId, userId);
            throw new InvalidParameterException("Request already canceled");
        } else if (temp.getStatus().equals(RequestStatus.PENDING)) {
            temp.setStatus(RequestStatus.CANCELED);
        } else if (temp.getStatus().equals(RequestStatus.CONFIRMED)) {
            temp.setStatus(RequestStatus.CANCELED);
            Event tempEvent = eventRepository.getReferenceById(temp.getEventId());
            tempEvent.setConfirmedRequests(tempEvent.getConfirmedRequests() - 1);
            eventRepository.save(tempEvent);
        }
        log.info("Request with id = {} from user with id = {} was cancelled", requestId, userId);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(temp));
    }

    private void validateEventId(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            log.info("Event with id = {} not found", eventId);
            throw new EventNotFoundException("Event not found");
        }
    }

    private void validateUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.info("User with id = {} not found", userId);
            throw new UserNotFoundException("User not found");
        }
    }

    private void validateRequestId(Long requestId) {
        if (!requestRepository.existsById(requestId)) {
            log.info("Request with id = {} not found", requestId);
            throw new RequestNotFoundException("Request not found");
        }
    }
}
