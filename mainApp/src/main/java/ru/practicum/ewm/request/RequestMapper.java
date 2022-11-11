package ru.practicum.ewm.request;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RequestMapper {
    public static Request toRequest(ParticipationRequestDto participationRequestDto) {
        Request request = new Request();
        request.setRequesterId(participationRequestDto.getRequester());
        request.setId(participationRequestDto.getId());
        request.setCreated(participationRequestDto.getCreated());
        request.setEventId(participationRequestDto.getEvent());
        request.setStatus(participationRequestDto.getStatus());
        return request;
    }

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
