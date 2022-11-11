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

    @Query("SELECT e FROM Event e WHERE e.initiator.id IN (?1) " +
            "AND e.category.id IN (?2) AND e.state IN (?3) " +
            "AND e.eventDate > ?4 AND e.eventDate < ?5")
    List<Event> findAllUsersEventsFull(List<Long> users, List<Long> categories, List<EventState> states,
                                       LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE" +
            " (upper(e.annotation) like upper(CONCAT('%',:text,'%')) or upper(e.description)" +
            " like upper(CONCAT('%',:text,'%')) or :text is null) and e.category IN :categories and" +
            " e.paid = :paid and e.eventDate > :start and e.eventDate < :end")
    List<Event> getFilteredEvents(String text, List<Integer> categories,
                                  Boolean paid, LocalDateTime start, LocalDateTime end,
                                  Pageable pageable);
}
