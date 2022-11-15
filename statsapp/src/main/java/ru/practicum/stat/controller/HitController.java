package ru.practicum.stat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stat.model.EndpointHit;
import ru.practicum.stat.model.ViewStats;
import ru.practicum.stat.service.HitServiceImpl;

import java.time.LocalDateTime;
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
    public EndpointHit createHit(@RequestBody EndpointHit endpointHit) {
        return hitService.createHit(endpointHit);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        return hitService.getStats(start, end, uris, unique);

    }

}
