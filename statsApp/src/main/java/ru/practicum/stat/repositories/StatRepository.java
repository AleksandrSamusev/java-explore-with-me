package ru.practicum.stat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stat.models.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT DISTINCT e.uri FROM EndpointHit e")
    List<String> getListOfUniqueUrisBetweenDates(LocalDateTime start, LocalDateTime end);

    @Query("select count(e) from EndpointHit e WHERE e.uri in ?3 and e.timestamp > ?1 and e.timestamp < ?2")
    Integer countAllWithUriBetweenDates(LocalDateTime decodedStart, LocalDateTime decodedEnd, String uri);

    @Query("select count(e) from EndpointHit e WHERE e.uri in ?3 and" +
            " e.timestamp > ?1 and e.timestamp < ?2 group by e.ip")
    Integer countAllWithUriBetweenDatesUnique(LocalDateTime decodedStart, LocalDateTime decodedEnd, String uri);
}
