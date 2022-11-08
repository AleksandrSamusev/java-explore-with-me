package ru.practicum.ewm.request;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RequestMapper {
    public static Request toRequest(ParticipationRequestDto participationRequestDto) {
        Request request = new Request();
        request.setRequesterId(participationRequestDto.getRequesterId());
        request.setId(participationRequestDto.getRequestId());
        request.setCreated(participationRequestDto.getCreated());
        request.setEventId(participationRequestDto.getEventId());
        request.setStatus(participationRequestDto.getStatus());
        return request;
    }

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setRequesterId(request.getRequesterId());
        participationRequestDto.setStatus(request.getStatus());
        participationRequestDto.setCreated(request.getCreated());
        participationRequestDto.setRequestId(request.getId());
        participationRequestDto.setEventId(request.getEventId());
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
