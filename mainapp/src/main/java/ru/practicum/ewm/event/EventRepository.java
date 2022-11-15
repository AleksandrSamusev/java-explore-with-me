package ru.practicum.ewm.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e WHERE e.category.id = ?1")
    List<Event> findAllEventsByCategory(Long id);

    @Query("SELECT e FROM Event e WHERE e.initiator.id = ?1")
    List<Event> findAllEventsByUserId(Long userId, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.initiator.id = ?1")
    List<Event> findAllEventsByUserId(Long userId);

    @Query("SELECT e FROM Event e WHERE e.initiator.id = ?1 and e.id = ?2")
    Event findEventByUserIdAAndEventId(Long userId, Long eventId);

    @Query("SELECT e FROM Event e WHERE (e.initiator.id IN (?1) OR (?1) is null) " +
            "AND (e.category.id IN (?2) or (?2) is null) AND (e.state IN (?3) or (?3) is null) " +
            "AND e.eventDate > ?4 AND e.eventDate < ?5")
    List<Event> findAllUsersEventsFull(List<Long> users, List<Long> categories, List<EventState> states,
                                       LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE" +
            " (upper(e.annotation) like upper(CONCAT('%',?1,'%')) or upper(e.description)" +
            " like upper(CONCAT('%',?1,'%')) or ?1 is null) and e.category.id IN (?2) and" +
            " e.paid = ?3 and e.eventDate > ?4 and e.eventDate < ?5")
    List<Event> getFilteredEvents(String text, List<Long> categories,
                                  Boolean paid, LocalDateTime start, LocalDateTime end,
                                  Pageable pageable);
}
