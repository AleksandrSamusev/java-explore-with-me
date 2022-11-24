package ru.practicum.ewm.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class ReviewControllerPublic {

    private final ReviewService reviewService;

    @Autowired
    public ReviewControllerPublic(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/events/{eventId}/reviews")
    public List<ShortReviewDto> findAllEventReviews(@PathVariable Long eventId) {
        return reviewService.findAllEventReviews(eventId);
    }

    @GetMapping("/{userId}/reviews")
    public List<ShortReviewDto> findAllUsersReviews(@PathVariable Long userId) {
        return reviewService.findAllUsersReviews(userId);
    }

    @GetMapping("/{userId}/events/{eventId}/reviews")
    public ShortReviewDto findUsersReviewToEvent(@PathVariable Long userId,
                                                 @PathVariable Long eventId) {
        return reviewService.findUsersReviewToEvent(userId, eventId);
    }

    @PostMapping("/{userId}/events/{eventId}/reviews")
    public NewReviewDto createReview(@Valid @RequestBody NewReviewDto newReviewDto,
                                     @PathVariable Long userId,
                                     @PathVariable Long eventId) {
        return reviewService.createReview(newReviewDto, userId, eventId);
    }

    @PatchMapping("/{userId}/reviews")
    public ShortReviewDto changeReview(@PathVariable Long userId,
                                       @Valid @RequestBody NewReviewDto newReviewDto) {
        return reviewService.changeReview(userId, newReviewDto);
    }


}
