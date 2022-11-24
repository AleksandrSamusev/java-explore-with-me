package ru.practicum.ewm.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("select r from Review r where r.event.id = :eventId")
    List<Review> findAllEventReviews(Long eventId);

    @Query("select r from Review r where r.reviewer.id = :userId")
    List<Review> findAllUsersReviews(Long userId);

    @Query("select r from Review r where r.reviewer.id = :userId and  r.event.id = :eventId")
    Review findUsersReviewToEvent(Long userId, Long eventId);
}
