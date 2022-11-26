package ru.practicum.stat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stat.model.EndpointHit;
import ru.practicum.stat.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select new ru.practicum.stat.model.ViewStats(h.app, h.uri, cast(count(h.id) as int))" +
            " from EndpointHit as h where h.timestamp >= :start and h.timestamp <= :end" +
            " group by h.app, h.uri")
    List<ViewStats> findAllByStartEndTimeNoUris(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.stat.model.ViewStats(h.app, h.uri, cast(count(h.id) as int))" +
            " from EndpointHit as h where h.timestamp >= :start and h.timestamp <= :end" +
            " group by h.app, h.uri, h.ip")
    List<ViewStats> findAllByStartEndTimeNoUrisUnique(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.stat.model.ViewStats(h.app, h.uri, cast(count(h.id) as int))" +
            " from EndpointHit as h where h.timestamp >= :start and h.timestamp <= :end" +
            " and h.uri IN :uris" +
            " group by h.app, h.uri, h.ip")
    List<ViewStats> findAllByStartEndTimeWithUrisUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.stat.model.ViewStats(h.app, h.uri, cast(count(h.id) as int))" +
            " from EndpointHit as h where h.timestamp >= :start and h.timestamp <= :end" +
            " and h.uri IN :uris" +
            " group by h.app, h.uri")
    List<ViewStats> findAllByStartEndTimeWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}
