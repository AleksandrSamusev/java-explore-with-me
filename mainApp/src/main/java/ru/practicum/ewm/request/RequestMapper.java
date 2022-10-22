package ru.practicum.ewm.request;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RequestMapper {
    public Request toRequest(ParticipationRequestDto participationRequestDto) {
        Request request = new Request();
        request.setRequesterId(participationRequestDto.getRequesterId());
        request.setRequestId(participationRequestDto.getRequestId());
        request.setCreated(participationRequestDto.getCreated());
        request.setEventId(participationRequestDto.getEventId());
        request.setStatus(participationRequestDto.getStatus());
        return request;
    }

    public ParticipationRequestDto toParticipationRequestDto(Request request) {
        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setRequesterId(request.getRequesterId());
        participationRequestDto.setStatus(request.getStatus());
        participationRequestDto.setCreated(request.getCreated());
        participationRequestDto.setRequestId(request.getRequestId());
        participationRequestDto.setEventId(request.getEventId());
        return participationRequestDto;
    }

    public List<ParticipationRequestDto> toParticipationRequestDtos(List<Request> requests) {
        List<ParticipationRequestDto> dtos = new ArrayList<>();
        for (Request request : requests) {
            dtos.add(toParticipationRequestDto(request));
        }
        return dtos;
    }

}
