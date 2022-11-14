package ru.practicum.stat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.stat.dto.EndpointHitDto;
import ru.practicum.stat.mapper.EndpointHitMapper;
import ru.practicum.stat.model.ViewStats;
import ru.practicum.stat.repository.HitRepository;

import java.util.List;

@Service
@Slf4j
public class HitServiceImpl implements HitService {
    private final HitRepository hitRepository;

    public HitServiceImpl(HitRepository hitRepository) {
        this.hitRepository = hitRepository;
    }

    @Override
    public EndpointHitDto createHit(EndpointHitDto endpointHitDto) {
        return EndpointHitMapper.toEndpointHitDto(hitRepository.save(EndpointHitMapper.toEndpointHit(endpointHitDto)));
    }

    @Override
    public List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique) {
        if (uris == null) {
            if (unique == null) {
                return hitRepository.findAllHitsBetweenDates(start, end);
            } else {
                return hitRepository.findAllHitsBetweenDatesUnique(start, end);
            }
        }
        if (unique == null) {
            return hitRepository.findAllHitsBetweenDatesByUris(start, end, uris);
        }
        return hitRepository.findAllHitsBetweenDatesByUrisAndUnique(start, end, uris);
    }
}
