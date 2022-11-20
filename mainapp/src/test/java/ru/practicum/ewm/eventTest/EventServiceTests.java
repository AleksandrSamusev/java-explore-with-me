package ru.practicum.ewm.eventTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.ewm.category.CategoryService;
import ru.practicum.ewm.category.NewCategoryDto;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.event.NewEventDto;
import ru.practicum.ewm.exception.InvalidParameterException;
import ru.practicum.ewm.location.Location;
import ru.practicum.ewm.user.NewUserRequest;
import ru.practicum.ewm.user.UserService;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=ewm",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class EventServiceTests<T extends EventService> {

    private final EntityManager em;
    private final EventService eventService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final EventRepository eventRepository;

    @Test
    public void givenValidEvent_WhenCreateEvent_thenEventCreated() {
        NewCategoryDto newCategoryDto = new NewCategoryDto("category");
        categoryService.createCategory(newCategoryDto);
        NewUserRequest newUserRequest = new NewUserRequest("user@user.com", "user");
        userService.createUser(newUserRequest);
        NewEventDto newEventDto = new NewEventDto();
        newEventDto.setEventDate(LocalDateTime.now().plusHours(5L));
        newEventDto.setTitle("Great event with a lot of interesting staff to look at");
        newEventDto.setPaid(false);
        newEventDto.setAnnotation("Great event with a lot of interesting staff to look at." +
                " You will see many interesting.");
        newEventDto.setCategory(1L);
        newEventDto.setDescription("Great event with a lot of interesting staff to look at." +
                " You will see many interesting. Do not miss it!");
        newEventDto.setLocation(new Location(12.12f, 23.22f));
        newEventDto.setRequestModeration(false);
        newEventDto.setParticipantLimit(0);
        eventService.createEvent(1L, newEventDto);
        assertThat(eventRepository.getReferenceById(1L).getTitle(),
                equalTo("Great event with a lot of interesting staff to look at"));
        List<Event> event = em.createQuery("select e from Event e", Event.class).getResultList();
        assertThat(event.size(), equalTo(1));
    }

    @Test
    public void givenAnnotationLengthLessThan20_WhenCreateEvent_thenException() {
        NewCategoryDto newCategoryDto = new NewCategoryDto("category");
        categoryService.createCategory(newCategoryDto);
        NewUserRequest newUserRequest = new NewUserRequest("user@user.com", "user");
        userService.createUser(newUserRequest);
        NewEventDto newEventDto = new NewEventDto();
        newEventDto.setEventDate(LocalDateTime.now().plusHours(5L));
        newEventDto.setTitle("Great event with a lot of interesting staff to look at");
        newEventDto.setPaid(false);
        newEventDto.setAnnotation("Great event.");
        newEventDto.setCategory(1L);
        newEventDto.setDescription("Great event with a lot of interesting staff to look at." +
                " You will see many interesting. Do not miss it!");
        newEventDto.setLocation(new Location(12.12f, 23.22f));
        newEventDto.setRequestModeration(false);
        newEventDto.setParticipantLimit(0);

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, newEventDto));
    }

    @Test
    public void givenAnnotationLengthMoreThan2000_WhenCreateEvent_thenException() {
        NewCategoryDto newCategoryDto = new NewCategoryDto("category");
        categoryService.createCategory(newCategoryDto);
        NewUserRequest newUserRequest = new NewUserRequest("user@user.com", "user");
        userService.createUser(newUserRequest);
        NewEventDto newEventDto = new NewEventDto();
        newEventDto.setEventDate(LocalDateTime.now().plusHours(5L));
        newEventDto.setTitle("Great event with a lot of interesting staff to look at");
        newEventDto.setPaid(false);
        newEventDto.setAnnotation(
                "123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
                        "123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
                        "123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
                        "123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
                        "123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
                        "123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
                        "123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
                        "123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
                        "123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
                        "123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
                        "123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
                        "123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
                        "123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
                        "123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
                        "123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
                        "123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
                        "123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
                        "123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
                        "123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
                        "123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
                        "123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
                        "123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
                        "123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
                        "123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891");
        newEventDto.setCategory(1L);
        newEventDto.setDescription("Great event with a lot of interesting staff to look at." +
                " You will see many interesting. Do not miss it!");
        newEventDto.setLocation(new Location(12.12f, 23.22f));
        newEventDto.setRequestModeration(false);
        newEventDto.setParticipantLimit(0);

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, newEventDto));

    }


}
