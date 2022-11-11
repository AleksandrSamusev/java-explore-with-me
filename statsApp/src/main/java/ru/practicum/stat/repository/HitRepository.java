package ru.practicum.stat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stat.model.EndpointHit;
import ru.practicum.stat.model.ViewStats;

import java.util.List;

public interface HitRepository extends JpaRepository<EndpointHit, Long> {
    @Query("select s from EndpointHit s where s.timestamp between :start and :end group by s.ip")
    List<ViewStats> findAllHitsBetweenDatesUnique(String start, String end);

    @Query("select s from EndpointHit s where s.timestamp between :start and :end and s.uri in :uris")
    List<ViewStats> findAllHitsBetweenDatesByUris(String start, String end, List<String> uris);

    @Query("select s from EndpointHit  s where s.timestamp between :start and :end")
    List<ViewStats> findAllHitsBetweenDates(String start, String end);

    @Query("select s from EndpointHit s where s.timestamp between :start and :end and s.uri in :uris group by s.ip")
    List<ViewStats> findAllHitsBetweenDatesByUrisAndUnique(String start, String end, List<String> uris);

}
