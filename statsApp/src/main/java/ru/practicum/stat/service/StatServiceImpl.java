package ru.practicum.stat.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.stat.models.EndpointHitDto;
import ru.practicum.stat.models.EndpointHitMapper;
import ru.practicum.stat.models.ViewStatsDto;
import ru.practicum.stat.repositories.StatRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatServiceImpl(StatRepository statRepository) {
        this.statRepository = statRepository;
    }

    public EndpointHitDto postHit(@RequestBody EndpointHitDto endpointHitDto) {
        return EndpointHitMapper.toEndpointHitDto(statRepository.save(EndpointHitMapper.toEndpointHit(endpointHitDto)));
    }

    public List<ViewStatsDto> getStatistics(String start, String end, List<String> uris,
                                            Boolean unique) {
        LocalDateTime encodedStart = LocalDateTime.parse(URLEncoder.encode(start, StandardCharsets.UTF_8), formatter);
        LocalDateTime encodedEnd = LocalDateTime.parse(URLEncoder.encode(end, StandardCharsets.UTF_8), formatter);
        List<ViewStatsDto> result = new ArrayList<>();
        if ((uris == null || uris.isEmpty()) && !unique) {
            List<String> ListOfUris = statRepository.getListOfUniqueUrisBetweenDates(encodedStart, encodedEnd);
            for (String uri : ListOfUris) {
                ViewStatsDto viewStatsDto = new ViewStatsDto();
                viewStatsDto.setApp("mainApp");
                viewStatsDto.setUri(uri);
                viewStatsDto.setHits(statRepository.countAllWithUriBetweenDates(encodedStart, encodedEnd, uri));
                result.add(viewStatsDto);
            }
            return result;
        } else if (uris == null || uris.isEmpty()) {
            List<String> ListOfUris = statRepository.getListOfUniqueUrisBetweenDates(encodedStart, encodedEnd);
            for (String uri : ListOfUris) {
                ViewStatsDto viewStatsDto = new ViewStatsDto();
                viewStatsDto.setApp("mainApp");
                viewStatsDto.setUri(uri);
                viewStatsDto.setHits(statRepository.countAllWithUriBetweenDatesUnique(encodedStart, encodedEnd, uri));
                result.add(viewStatsDto);
            }
            return result;
        } else if (!unique) {
            for (String uri : uris) {
                ViewStatsDto viewStatsDto = new ViewStatsDto();
                viewStatsDto.setApp("mainApp");
                viewStatsDto.setUri(uri);
                viewStatsDto.setHits(statRepository.countAllWithUriBetweenDates(encodedStart, encodedEnd, uri));
                result.add(viewStatsDto);
            }
            return result;
        }
        for (String uri : uris) {
            ViewStatsDto viewStatsDto = new ViewStatsDto();
            viewStatsDto.setApp("mainApp");
            viewStatsDto.setUri(uri);
            viewStatsDto.setHits(statRepository.countAllWithUriBetweenDatesUnique(encodedStart, encodedEnd, uri));
            result.add(viewStatsDto);
        }
        return result;
    }
}
