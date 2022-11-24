package ru.practicum.ewm.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.EventNotFoundException;
import ru.practicum.ewm.exception.InvalidParameterException;
import ru.practicum.ewm.exception.ReviewNotFoundException;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.user.UserRepository;

import java.util.List;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, EventRepository eventRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }


    @Override
    public List<ShortReviewDto> findAllEventReviews(Long eventId) {
        validateEventId(eventId);
        return ReviewMapper.toShortReviewDtos(reviewRepository.findAllEventReviews(eventId));
    }

    @Override
    public List<ShortReviewDto> findAllUsersReviews(Long userId) {
        validateUserId(userId);
        return ReviewMapper.toShortReviewDtos(reviewRepository.findAllUsersReviews(userId));
    }

    @Override
    public ShortReviewDto findUsersReviewToEvent(Long userId, Long eventId) {
        validateUserId(userId);
        validateEventId(eventId);
        return ReviewMapper.toShortReviewDto(reviewRepository.findUsersReviewToEvent(userId, eventId));
    }

    @Override
    public NewReviewDto createReview(NewReviewDto newReviewDto, Long userId, Long eventId) {
        validateEventId(eventId);
        validateUserId(userId);
        Review review = ReviewMapper.toReview(newReviewDto);
        review.setReviewer(userRepository.getReferenceById(userId));
        review.setEvent(eventRepository.getReferenceById(eventId));
        NewReviewDto dto = ReviewMapper.toNewReviewDto(reviewRepository.save(review));
        log.info("review (id = {}) created", dto.getId());

        //set new rating

        Event event = eventRepository.getReferenceById(eventId);
        event.setRating(calculateEventRating(eventId));
        eventRepository.save(event);
        log.info("new rating calculated: eventId - {}, rating - {}", eventId, event.getRating());

        return dto;
    }

    @Override
    public ShortReviewDto changeReview(Long userId, NewReviewDto newReviewDto) {
        validateChangedReview(newReviewDto);
        validateUserId(userId);
        Review review = reviewRepository.getReferenceById(newReviewDto.getId());
        if (newReviewDto.getComment() != null) {
            review.setComment(newReviewDto.getComment());
        }
        log.info("review (id = {}) changed", newReviewDto.getId());
        return ReviewMapper.toShortReviewDto(reviewRepository.save(review));
    }

    private void validateUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.info("User with id = {} not found", userId);
            throw new UserNotFoundException("User not found");
        }
    }

    private void validateEventId(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            log.info("Event with id = {} not found", eventId);
            throw new EventNotFoundException("Event not found");
        }
    }

    private void validateChangedReview(NewReviewDto dto) {
        if (dto.getId() == null) {
            log.info("mandatory parameter id is null");
            throw new InvalidParameterException("mandatory parameter id is null");
        } else if (dto.getId() <= 0) {
            log.info("parameter id <= 0");
            throw new InvalidParameterException("parameter id less or even to 0");
        } else if (!reviewRepository.existsById(dto.getId())) {
            log.info("review (id = {}) not found", dto.getId());
            throw new ReviewNotFoundException("review not found");
        } else if (!eventRepository.existsById(dto.getEventId())) {
            log.info("event (id = {}) not found", dto.getEventId());
            throw new EventNotFoundException("event not found");
        } else if (!userRepository.existsById(dto.getReviewerId())) {
            log.info("user (id = {}) not found", dto.getReviewerId());
            throw new UserNotFoundException("user not found");
        }
    }

    private double calculateEventRating(Long eventId) {

        List<Review> list = reviewRepository.findAllEventReviews(eventId);
        int positiveReviews = (int) list.stream()
                .filter(review -> review.getReview().equals(Boolean.TRUE))
                .count();
        int negativeReviews = (int) list.stream()
                .filter(review -> review.getReview().equals(Boolean.FALSE))
                .count();
        int totalVotes = positiveReviews + negativeReviews;
        double average = (double) positiveReviews / totalVotes;
        double score = average - (average - 0.5) * Math.pow(2, -Math.log10(totalVotes + 1));
        return score * 10;

    }
}
