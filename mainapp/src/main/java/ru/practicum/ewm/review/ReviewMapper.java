package ru.practicum.ewm.review;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.user.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReviewMapper {

    public static Review toReview(NewReviewDto newReviewDto) {
        Review review = new Review();
        review.setComment(newReviewDto.getComment());
        review.setReview(newReviewDto.getReview());
        review.setCreated(LocalDateTime.now());
        return review;
    }

    public static NewReviewDto toNewReviewDto(Review review) {
        NewReviewDto newReviewDto = new NewReviewDto();
        newReviewDto.setId(review.getId());
        newReviewDto.setReviewerId(review.getReviewer().getId());
        newReviewDto.setReview(review.getReview());
        newReviewDto.setComment(review.getComment());
        newReviewDto.setEventId(review.getEvent().getId());
        return newReviewDto;
    }

    public static FullReviewDto toFullReviewDto(Review review) {
        FullReviewDto fullReviewDto = new FullReviewDto();
        fullReviewDto.setId(review.getId());
        fullReviewDto.setReviewer(UserMapper.toUserShortDto(review.getReviewer()));
        fullReviewDto.setEvent(EventMapper.toEventShortDto(review.getEvent()));
        fullReviewDto.setReview(review.getReview());
        fullReviewDto.setComment(review.getComment());
        fullReviewDto.setCreated(review.getCreated());
        return fullReviewDto;
    }

    public static ShortReviewDto toShortReviewDto(Review review) {
        ShortReviewDto shortReviewDto = new ShortReviewDto();
        shortReviewDto.setReviewerName(review.getReviewer().getName());
        shortReviewDto.setComment(review.getComment());
        shortReviewDto.setReview(review.getReview());
        shortReviewDto.setCreated(review.getCreated());
        return shortReviewDto;
    }

    public static List<Review> toReviews(List<NewReviewDto> newReviewDtos) {
        List<Review> reviews = new ArrayList<>();
        for (NewReviewDto newReviewDto : newReviewDtos) {
            reviews.add(toReview(newReviewDto));
        }
        return reviews;
    }

    public static List<NewReviewDto> toNewReviewDtos(List<Review> reviews) {
        List<NewReviewDto> newReviewDtos = new ArrayList<>();
        for (Review review : reviews) {
            newReviewDtos.add(toNewReviewDto(review));
        }
        return newReviewDtos;
    }

    public static List<ShortReviewDto> toShortReviewDtos(List<Review> reviews) {
        List<ShortReviewDto> shortReviewDtos = new ArrayList<>();
        for (Review review : reviews) {
            shortReviewDtos.add(toShortReviewDto(review));
        }
        return shortReviewDtos;
    }
}
