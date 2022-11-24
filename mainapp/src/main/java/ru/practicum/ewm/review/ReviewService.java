package ru.practicum.ewm.review;

import java.util.List;

public interface ReviewService {
    List<ShortReviewDto> findAllEventReviews(Long eventId);
}
