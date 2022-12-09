package ru.practicum.ewm.review;

import java.util.List;

public interface ReviewService {

    List<ShortReviewDto> findAllEventReviews(Long eventId);

    List<ShortReviewDto> findAllUsersReviews(Long userId);

    ShortReviewDto findUsersReviewToEvent(Long userId, Long eventId);

    NewReviewDto createReview(NewReviewDto newReviewDto, Long userId, Long eventId);

    ShortReviewDto changeReview(Long userId, Long reviewId, NewReviewDto newReviewDto);

    void deleteReviewById(Long reviewId);
}
