package ru.practicum.stat.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.stat.dto.EndpointHitDto;
import ru.practicum.stat.model.EndpointHit;

import java.util.ArrayList;
import java.util.List;

@Component
public class EndpointHitMapper {
    public static EndpointHitDto toEndpointHitDto(EndpointHit endpointHit) {
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp(endpointHit.getApp());
        endpointHitDto.setIp(endpointHit.getIp());
        endpointHitDto.setId(endpointHit.getId());
        endpointHitDto.setUri(endpointHit.getUri());
        endpointHitDto.setTimestamp(endpointHit.getTimestamp());
        return endpointHitDto;
    }

    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(endpointHitDto.getApp());
        endpointHit.setUri(endpointHitDto.getUri());
        endpointHit.setTimestamp(endpointHitDto.getTimestamp());
        endpointHit.setId(endpointHitDto.getId());
        endpointHit.setIp(endpointHitDto.getIp());
        return endpointHit;
    }

    public static List<EndpointHit> toEndpointHits(List<EndpointHitDto> endpointHitDtos) {
        List<EndpointHit> endpointHits = new ArrayList<>();
        for (EndpointHitDto endpointHitDto : endpointHitDtos) {
            endpointHits.add(toEndpointHit(endpointHitDto));
        }
        return endpointHits;
    }

    public static List<EndpointHitDto> toEndpointHitDtos(List<EndpointHit> endpointHits) {
        List<EndpointHitDto> endpointHitDtos = new ArrayList<>();
        for (EndpointHit endpointHit : endpointHits) {
            endpointHitDtos.add(toEndpointHitDto(endpointHit));
        }
        return endpointHitDtos;
    }
}
