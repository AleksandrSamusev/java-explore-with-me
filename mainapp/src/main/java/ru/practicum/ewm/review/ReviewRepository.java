package ru.practicum.ewm.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("select r from Request r where r.eventId = :eventId")
    List<Review> findAllEventReviews(Long eventId);
}
