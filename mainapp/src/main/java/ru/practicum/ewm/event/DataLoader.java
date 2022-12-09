package ru.practicum.ewm.event;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;

@Component
@Data
public class DataLoader {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public void loadData() {
        Event event = Event.builder()
                .eventDate(LocalDateTime.now().minusMonths(5L))
                .createdOn(LocalDateTime.now().minusMonths(9L))
                .publishedOn(LocalDateTime.now().minusMonths(8L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(categoryRepository.getReferenceById(1L))
                .description("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .lat(12.12f)
                .lon(23.22f)
                .requestModeration(false)
                .participantLimit(0)
                .initiator(userRepository.getReferenceById(1L))
                .state(EventState.PUBLISHED)
                .available(true)
                .ratingFlag(true)
                .build();

        eventRepository.save(event);

        Event event2 = Event.builder()
                .eventDate(LocalDateTime.now().minusMonths(4L))
                .createdOn(LocalDateTime.now().minusMonths(8L))
                .publishedOn(LocalDateTime.now().minusMonths(7L))
                .title("Great event 2 with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event 2 with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(categoryRepository.getReferenceById(1L))
                .description("Great event 2 with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .lat(34f)
                .lon(67f)
                .requestModeration(false)
                .participantLimit(0)
                .initiator(userRepository.getReferenceById(1L))
                .state(EventState.PUBLISHED)
                .available(true)
                .ratingFlag(true)
                .build();

        eventRepository.save(event2);

        Event event3 = Event.builder()
                .eventDate(LocalDateTime.now().minusMonths(4L))
                .createdOn(LocalDateTime.now().minusMonths(8L))
                .publishedOn(LocalDateTime.now().minusMonths(7L))
                .title("Great event 3 with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event 3 with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(categoryRepository.getReferenceById(1L))
                .description("Great event 3 with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .lat(68.1f)
                .lon(11.09f)
                .requestModeration(false)
                .participantLimit(0)
                .initiator(userRepository.getReferenceById(1L))
                .state(EventState.PUBLISHED)
                .available(true)
                .ratingFlag(true)
                .build();

        eventRepository.save(event3);
    }

}
