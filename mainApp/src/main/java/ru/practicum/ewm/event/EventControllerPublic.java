package ru.practicum.ewm.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.stat.models.EndpointHit;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/events")
public class EventControllerPublic {

    private final URI uri = URI.create("${STAT_SERVER_URL} + /hit");
    private final HttpClient client = HttpClient.newHttpClient();
    private final EventServiceImpl eventService;

    @Autowired
    public EventControllerPublic(EventServiceImpl eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventShortDto> getSortedEvents(@RequestParam String text,
                                               @RequestParam List<Integer> categories,
                                               @RequestParam Boolean paid,
                                               @RequestParam String rangeStart,
                                               @RequestParam String rangeEnd,
                                               @RequestParam Boolean onlyAvailable,
                                               @RequestParam String sort,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               HttpServletRequest request) throws IOException, InterruptedException {
        saveStatistic(request);
        return eventService.getSortedEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort,
                from, size);
    }

    private void saveStatistic(HttpServletRequest request) throws IOException, InterruptedException {
        HttpRequest statRequest = HttpRequest.newBuilder()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(EndpointHit.builder()
                        .app("mainApp").timestamp(LocalDateTime.now()).uri(request.getRequestURI())
                        .ip(request.getRemoteAddr()).build().toString()))
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(statRequest, handler);

    }
}
