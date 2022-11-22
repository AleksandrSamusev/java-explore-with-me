package ru.practicum.ewm.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users/{userId}/requests")
public class RequestControllerPrivate {
    private final RequestService requestService;

    @Autowired
    public RequestControllerPrivate(RequestServiceImpl requestService) {
        this.requestService = requestService;
    }

    @GetMapping()
    public List<ParticipationRequestDto> findAllUsersRequests(@PathVariable Long userId) {
        return requestService.findAllUsersRequests(userId);
    }

    @PostMapping
    public ParticipationRequestDto createRequestFromCurrentUser(@PathVariable Long userId,
                                                                @RequestParam Long eventId) {
        return requestService.createRequestFromCurrentUser(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelOwnRequest(@PathVariable Long userId,
                                                    @PathVariable Long requestId) {
        return requestService.cancelOwnRequest(userId, requestId);
    }
}
