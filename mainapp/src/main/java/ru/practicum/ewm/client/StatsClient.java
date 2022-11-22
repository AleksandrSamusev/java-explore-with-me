package ru.practicum.ewm.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.client.model.EndpointHit;
import ru.practicum.ewm.client.model.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
public class StatsClient {

    private final RestTemplate restTemplate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClient(@Value("${stats.server.url}") String serverUrl, RestTemplateBuilder builder) {
        this.restTemplate = builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .build();
    }

    public void saveStats(EndpointHit endpointHit) {
        log.info("Save statistics: app = \"{}\" , uri = \"{}\"", endpointHit.getApp(), endpointHit.getUri());
        restTemplate.postForObject("/hit", endpointHit, String.class);
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique)
            throws JsonProcessingException {
        StringBuilder builder = new StringBuilder(String.format("/stats?start=%s&end=%s",
                start.format(formatter),
                end.format(formatter)));
        if (uris != null) {
            builder.append(String.format("&uris=%s", String.join(",", uris)));
        }
        if (unique != null) {
            builder.append(String.format("&unique=%s", unique));
        }
        log.info("request parameters: start - {}, end - {}, uris - {}, unique - {}", start, end, uris, unique);
        ResponseEntity<String> response = restTemplate.getForEntity(builder.toString(), String.class);
        log.info("response status: {}", response.getStatusCode());
        String jsonString = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonString, new TypeReference<>() {
        });
    }
}
