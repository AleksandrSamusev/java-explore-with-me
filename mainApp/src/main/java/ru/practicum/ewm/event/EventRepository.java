package ru.practicum.ewm.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e WHERE e.category.categoryId = ?1")
    List<Event> findAllEventsByCategory(Long categoryId);
}
