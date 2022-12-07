package ru.practicum.ewm.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.InvalidParameterException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.request.RequestStatus;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, EventRepository eventRepository, UserRepository userRepository, RequestRepository requestRepository) {
        this.reviewRepository = reviewRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
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

        //check if event took place
        if (eventRepository.getReferenceById(eventId).getEventDate().isAfter(LocalDateTime.now())) {
            log.info("the event (id - {}) has not happened yet", eventId);
            throw new ForbiddenException("the event has not happened yet");
        }

        // check if user has a confirmed request to this event (if user is participant)
        if (requestRepository.findAllConfirmedRequestsByEventId(eventId).stream()
                .noneMatch(request -> request.getRequesterId().equals(userId))) {
            log.info("user (id - {}) not a participant of event (id - {})", userId, eventId);
            throw new ForbiddenException("Only participant can create review");
        }
        //check if user is not an initiator
        if (Objects.equals(eventRepository.getReferenceById(eventId).getInitiator().getId(), userId)) {
            log.info("rejected. user (id = {}) is an initiator of event (id={})", userId, eventId);
            throw new ForbiddenException("Review can't be created by initiator");
        }

        //check if review from this user to this event already exists
        if (reviewRepository.findUsersReviewToEvent(userId, eventId) != null) {
            log.info("review to event (id - {}) from user (id - {}) already exists", eventId, userId);
            throw new ForbiddenException("review from this user to this event already exists");
        }

        //...if he is a participant - create review
        Review review = ReviewMapper.toReview(newReviewDto);
        review.setReviewer(userRepository.getReferenceById(userId));
        review.setEvent(eventRepository.getReferenceById(eventId));
        NewReviewDto dto = ReviewMapper.toNewReviewDto(reviewRepository.save(review));
        log.info("review (id = {}) created", dto.getId());

        //set new initiator rating
        User user = userRepository.getReferenceById(eventRepository.getReferenceById(eventId).getInitiator().getId());
        double currentRating = user.getInitiatorRating();
        user.setInitiatorRating(round(calculateInitiatorRating(eventId), 2));
        User savedUser = userRepository.save(user);
        log.info("new rating calculated: userId - {}, current rating - {},  new rating - {}",
                user.getId(), currentRating, savedUser.getInitiatorRating());

        //set new reviewer rating
        User reviewer = userRepository.getReferenceById(userId);
        reviewer.setReviewerRating(round(calculateReviewerRating(userId), 2));
        userRepository.save(reviewer);
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
            throw new NotFoundException("User not found");
        }
    }

    private void validateEventId(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            log.info("Event with id = {} not found", eventId);
            throw new NotFoundException("Event not found");
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
            throw new NotFoundException("review not found");
        }
    }

    private double calculateInitiatorRating(Long eventId) {

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

    private double calculateReviewerRating(Long userId) {
        int allParticipated = (int) requestRepository.findAllRequestsByUserId(userId).stream()
                .filter(request -> request.getStatus().equals(RequestStatus.CONFIRMED))
                .count();
        int allReviewed = (int) reviewRepository.findAllUsersReviews(userId).stream().count();
        if (allParticipated != 0) {
            return (double) (allReviewed / allParticipated) * 10;
        } else {
            return 0;
        }
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
