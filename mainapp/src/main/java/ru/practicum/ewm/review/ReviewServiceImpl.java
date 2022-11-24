package ru.practicum.ewm.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.EventNotFoundException;

import java.util.List;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final EventRepository eventRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, EventRepository eventRepository) {
        this.reviewRepository = reviewRepository;
        this.eventRepository = eventRepository;
    }


    @Override
    public List<ShortReviewDto> findAllEventReviews(Long eventId) {
        validateEventId(eventId);
        return ReviewMapper.toShortReviewDtos(reviewRepository.findAllEventReviews(eventId));
    }

    private void validateEventId(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            log.info("Event with id = {} not found", eventId);
            throw new EventNotFoundException("Event not found");
        }
    }
}
