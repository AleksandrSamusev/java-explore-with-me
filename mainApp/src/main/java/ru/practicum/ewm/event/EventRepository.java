package ru.practicum.ewm.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e WHERE e.category.categoryId = ?1")
    List<Event> findAllEventsByCategory(Long categoryId);

    @Query("SELECT e FROM Event e WHERE e.initiator.userId = ?1")
    List<Event> findAllEventsByUserId(Long userId, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.initiator.userId = ?1 and e.eventId = ?2")
    Event findEventByUserIdAAndEventId(Long userId, Long eventId);


}
