package ru.practicum.ewm.request;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setRequester(request.getRequesterId());
        participationRequestDto.setStatus(request.getStatus());
        participationRequestDto.setCreated(request.getCreated());
        participationRequestDto.setId(request.getId());
        participationRequestDto.setEvent(request.getEventId());
        return participationRequestDto;
    }

    public static List<ParticipationRequestDto> toParticipationRequestDtos(List<Request> requests) {
        List<ParticipationRequestDto> dtos = new ArrayList<>();
        for (Request request : requests) {
            dtos.add(toParticipationRequestDto(request));
        }
        return dtos;
    }

}
