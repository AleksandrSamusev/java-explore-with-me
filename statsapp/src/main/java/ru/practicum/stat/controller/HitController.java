package ru.practicum.stat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stat.dto.EndpointHitDto;
import ru.practicum.stat.model.ViewStats;
import ru.practicum.stat.service.HitServiceImpl;

import java.util.List;

@RestController
@Slf4j
public class HitController {
    private final HitServiceImpl hitService;

    @Autowired
    public HitController(HitServiceImpl hitService) {
        this.hitService = hitService;
    }

    @PostMapping("/hit")
    public EndpointHitDto createHit(@RequestBody EndpointHitDto endpointHitDto) {
        return hitService.createHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam String start,
                                    @RequestParam String end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(required = false) Boolean unique) {
        return hitService.getStats(start, end, uris, unique);

    }

}
