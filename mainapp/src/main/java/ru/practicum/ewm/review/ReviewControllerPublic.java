package ru.practicum.ewm.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("events/{eventId}/reviews")
    public List<ShortReviewDto> findAllEventReviews(@PathVariable Long eventId) {
        return reviewService.findAllEventReviews(eventId);
    }

}
