package ru.practicum.stat.service;

import ru.practicum.stat.dto.EndpointHitDto;
import ru.practicum.stat.model.ViewStats;

import java.util.List;

public interface HitService {
    EndpointHitDto createHit(EndpointHitDto endpointHitDto);

    List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique);
}
