package ru.practicum.ewm.request;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.exception.EventNotFoundException;
import ru.practicum.ewm.exception.InvalidParameterException;
import ru.practicum.ewm.exception.RequestNotFoundException;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.user.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public RequestServiceImpl(RequestRepository requestRepository, UserRepository userRepository, EventRepository eventRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    public List<ParticipationRequestDto> findAllUsersRequests(Long userId) {
        validateUserId(userId);
        return RequestMapper.toParticipationRequestDtos(requestRepository.findAllRequestsByUserId(userId));
    }

    public ParticipationRequestDto createRequestFromCurrentUser(Long userId, Long eventId) {
        validateUserId(userId);
        validateEventId(eventId);
        List<Request> usersRequests = requestRepository.findAllRequestsByUserId(userId);
        for (Request request : usersRequests) {
            if (Objects.equals(request.getEventId(), eventId)) {
                throw new InvalidParameterException("Denied. Request already created");
            }
        }
        if (Objects.equals(eventRepository.getReferenceById(eventId).getInitiator().getUserId(), userId)) {
            throw new InvalidParameterException("Denied. Can't be requested by initiator");
        } else if (!eventRepository.getReferenceById(eventId).getState().equals(EventState.PUBLISHED)) {
            throw new InvalidParameterException("Denied. Event is not published");
        } else if (requestRepository.findAllConfirmedRequestsByEventId(eventId).size() ==
                eventRepository.getReferenceById(eventId).getParticipantLimit()) {
            throw new InvalidParameterException("Denied. Participants limit reached.");
        } else if (!eventRepository.getReferenceById(eventId).getRequestModeration()) {
            Request request = requestRepository.findAllPendingRequestsByEventIdAndUserId(userId, eventId).get(0);
            request.setStatus(RequestStatus.CONFIRMED);
            Event temp = eventRepository.getReferenceById(eventId);
            temp.setConfirmedRequests(temp.getConfirmedRequests() + 1L);
            eventRepository.save(temp);
            return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
        }
        Request request = requestRepository.findAllPendingRequestsByEventIdAndUserId(userId, eventId).get(0);
        request.setStatus(RequestStatus.CONFIRMED);
        Event temp = eventRepository.getReferenceById(eventId);
        temp.setConfirmedRequests(temp.getConfirmedRequests() + 1L);
        eventRepository.save(temp);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    public ParticipationRequestDto cancelOwnRequest(Long userId, Long requestId) {
        validateUserId(userId);
        validateRequestId(requestId);
        Request temp = requestRepository.getReferenceById(requestId);
        temp.setStatus(RequestStatus.CANCELLED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(temp));
    }

    private void validateEventId(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Event not found");
        }
    }

    private void validateUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }
    }

    private void validateRequestId(Long requestId) {
        if (!requestRepository.existsById(requestId)) {
            throw new RequestNotFoundException("Request not found");
        }
    }
}
