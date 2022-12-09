package ru.practicum.stat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.stat.model.EndpointHit;
import ru.practicum.stat.model.ViewStats;
import ru.practicum.stat.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class HitServiceImpl implements HitService {
    private final HitRepository hitRepository;

    public HitServiceImpl(HitRepository hitRepository) {
        this.hitRepository = hitRepository;
    }

    @Override
    public EndpointHit createHit(EndpointHit endpointHit) {
        return (hitRepository.save(endpointHit));
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        List<ViewStats> stats;
        if (uris.isEmpty()) {
            if (unique) {
                stats = hitRepository.findAllByStartEndTimeNoUrisUnique(start, end);
            } else {
                stats = hitRepository.findAllByStartEndTimeNoUris(start, end);
            }
        } else {
            if (unique) {
                stats = hitRepository.findAllByStartEndTimeWithUrisUnique(start, end, uris);
            } else {
                stats = hitRepository.findAllByStartEndTimeWithUris(start, end, uris);
            }
        }
        return stats;
    }
}
