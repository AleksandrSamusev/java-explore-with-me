package ru.practicum.stat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.stat.models.EndpointHitDto;
import ru.practicum.stat.models.ViewStatsDto;
import ru.practicum.stat.service.StatServiceImpl;

import java.util.List;

@RestController
@Slf4j

public class StatController {
    private final StatServiceImpl statService;

    @Autowired
    public StatController(StatServiceImpl statService) {
        this.statService = statService;
    }

    @PostMapping("/hit")
    EndpointHitDto postHit(EndpointHitDto endpointHitDto) {
        return statService.postHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStatistics(String start, String end, @RequestParam List<String> uris,
                                            @RequestParam Boolean unique) {
        return statService.getStatistics(start, end, uris, unique);
    }


}
