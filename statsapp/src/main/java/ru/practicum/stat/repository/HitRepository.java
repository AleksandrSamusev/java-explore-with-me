package ru.practicum.stat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.stat.model.EndpointHit;
import ru.practicum.stat.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<EndpointHit, Long> {

    @Query(value = "select " +
            "   new ru.practicum.stat.model.ViewStats(h.app, h.uri, cast((1) as int))" +
            "from EndpointHit as h " +
            "where h.timestamp >= :start " +
            "and h.timestamp <= :end " +
            "and (:uris is null or h.uri in :uris) " +
            "group by h.app, h.uri, case when (:unique = true) then h.ip else cast('ip' as string) end ")
    List<ViewStats> findAllByStartEndTime(LocalDateTime start,
                                          LocalDateTime end,
                                          @Param("uris") List<String> uris,
                                          @Param("unique") boolean unique);

}
