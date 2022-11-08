package ru.practicum.stat.models;

import org.springframework.stereotype.Component;

@Component
public class EndpointHitMapper {
    public static EndpointHit toEndpointHit(EndpointHitDto endPointHitDto) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(endPointHitDto.getApp());
        endpointHit.setId(endPointHitDto.getId());
        endpointHit.setIp(endPointHitDto.getIp());
        endpointHit.setUri(endPointHitDto.getUri());
        endpointHit.setTimestamp(endPointHitDto.getTimestamp());
        return endpointHit;
    }

    public static EndpointHitDto toEndpointHitDto(EndpointHit endPointHit) {
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp(endPointHit.getApp());
        endpointHitDto.setId(endPointHit.getId());
        endpointHitDto.setIp(endPointHit.getIp());
        endpointHitDto.setUri(endPointHit.getUri());
        endpointHitDto.setTimestamp(endPointHit.getTimestamp());
        return endpointHitDto;
    }
}
