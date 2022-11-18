package ru.practicum.ewm.request;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> findAllUsersRequests(Long userId);

    ParticipationRequestDto createRequestFromCurrentUser(Long userId, Long eventId);

    ParticipationRequestDto cancelOwnRequest(Long userId, Long requestId);
}
