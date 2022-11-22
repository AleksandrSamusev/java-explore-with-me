package ru.practicum.ewm.eventTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.ewm.category.CategoryService;
import ru.practicum.ewm.category.NewCategoryDto;
import ru.practicum.ewm.event.*;
import ru.practicum.ewm.exception.EventNotFoundException;
import ru.practicum.ewm.exception.InvalidParameterException;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.location.Location;
import ru.practicum.ewm.request.ParticipationRequestDto;
import ru.practicum.ewm.request.RequestService;
import ru.practicum.ewm.request.RequestStatus;
import ru.practicum.ewm.user.NewUserRequest;
import ru.practicum.ewm.user.UserService;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.ewm.event.EventState.CANCELED;

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
    private final RequestService requestService;

    @BeforeEach
    public void initData() {
        NewCategoryDto newCategoryDto = new NewCategoryDto("category");
        categoryService.createCategory(newCategoryDto);
        NewUserRequest newUserRequest = new NewUserRequest("user@user.com", "user");
        userService.createUser(newUserRequest);
    }

    @Test
    public void givenValidEvent_WhenCreateEvent_thenEventCreated() {

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(1L)
                .description("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        eventService.createEvent(1L, event);

        assertThat(em.createQuery("select e from Event e", Event.class)
                .getResultList().size(), equalTo(1));
    }

    @Test
    public void givenAnnotationLengthLessThan20_WhenCreateEvent_thenException() {

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event")
                .category(1L)
                .description("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, event));
    }

    @Test
    public void givenAnnotationLengthMoreThan2000_WhenCreateEvent_thenException() {

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("12345678911234567891123456789112345678911234567891123456789112345678911234567891" +
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
                        "123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891")
                .category(1L)
                .description("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, event));
    }

    @Test
    public void givenEventDateLessThen2hrs_WhenCreateEvent_thenException() {

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(1L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(1L)
                .description("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, event));
    }

    @Test
    public void givenCategoryIsNull_WhenCreateEvent_thenException() {

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(null)
                .description("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, event));
    }

    @Test
    public void givenDescriptionIsNull_WhenCreateEvent_thenException() {

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(1L)
                .description(null)
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, event));
    }

    @Test
    public void givenDescriptionIsBlank_WhenCreateEvent_thenException() {

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(1L)
                .description("    ")
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, event));
    }

    @Test
    public void givenEventWithDescriptionLessThen20_WhenCreateEvent_thenException() {

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(1L)
                .description("Great event!")
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, event));
    }

    @Test
    public void givenEventWithDescriptionMoreThen7000_WhenCreateEvent_thenException() {

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(1L)
                .description("12345678911234567891123456789112345678911234567891123456789112345678911234567891" +
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
                        "123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891"
                )
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, event));
    }

    @Test
    public void givenEventDateInPast_WhenCreateEvent_thenException() {

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().minusHours(2L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(1L)
                .description("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, event));
    }

    @Test
    public void givenLatIsNull_WhenCreateEvent_thenException() {

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(1L)
                .description("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .location(new Location(null, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, event));
    }

    @Test
    public void givenLonIsNull_WhenCreateEvent_thenException() {

        NewEventDto newEventDto = new NewEventDto();
        newEventDto.setEventDate(LocalDateTime.now().plusHours(5L));
        newEventDto.setTitle("Great event with a lot of interesting staff to look at");
        newEventDto.setPaid(false);
        newEventDto.setAnnotation("Great event with a lot of interesting staff to look at." +
                " You will see many interesting.");
        newEventDto.setCategory(1L);
        newEventDto.setDescription("Great event with a lot of interesting staff to look at." +
                " You will see many interesting. Do not miss it!");
        newEventDto.setLocation(new Location(23.22f, null));
        newEventDto.setRequestModeration(false);
        newEventDto.setParticipantLimit(0);

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, newEventDto));
    }

    @Test
    public void givenLocationIsNull_WhenCreateEvent_thenException() {

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(1L)
                .description("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .location(null)
                .requestModeration(false)
                .participantLimit(0)
                .build();

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, event));
    }

    @Test
    public void givenTitleIsNull_WhenCreateEvent_thenException() {

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title(null)
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(1L)
                .description("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, event));
    }

    @Test
    public void givenTitleIsBlank_WhenCreateEvent_thenException() {

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title("   ")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(1L)
                .description("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, event));
    }

    @Test
    public void givenUserExistsAndEventExists_WhenGetUsersEvents_ThenReturnEventList() {

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(1L)
                .description("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        eventService.createEvent(1L, event);

        assertThat(eventService.findAllUsersEvents(1L, 0, 10)
                .size(), equalTo(1));
        assertThat(eventService.findAllUsersEvents(1L, 0, 10)
                .get(0).getInitiator().getName(), equalTo("user"));
    }

    @Test
    public void givenUserNotExists_WhenGetUsersEvents_ThenException() {

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(1L)
                .description("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        eventService.createEvent(1L, event);

        assertThrows(UserNotFoundException.class,
                () -> eventService.findAllUsersEvents(999L, 0, 10));
    }

    @Test
    public void givenUserExistsAndValidRequest_WhenPatchEvent_ThenEventPatched() {

        NewCategoryDto newCategory = new NewCategoryDto("category2");

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(1L)
                .description("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        UpdateEventRequest request = UpdateEventRequest.builder()
                .eventDate(LocalDateTime.now().plusHours(8L))
                .eventId(1L)
                .paid(true)
                .description("This description was updated")
                .annotation("this is an updated annotation")
                .participantLimit(999)
                .categoryId(2L)
                .build();

        categoryService.createCategory(newCategory);
        eventService.createEvent(1L, event);

        EventFullDto patched = eventService.patchEvent(1L, request);

        assertThat(patched.getId(), equalTo(1L));
        assertThat(patched.getAnnotation(), equalTo("this is an updated annotation"));
        assertThat(patched.getTitle(), equalTo("Great event with a lot of interesting staff to look at"));
        assertThat(patched.getDescription(), equalTo("This description was updated"));
        assertThat(patched.getPaid(), equalTo(true));
        assertThat(patched.getParticipantLimit(), equalTo(999));
        assertThat(patched.getCategory().getId(), equalTo(2L));
    }

    @Test
    public void givenUserExistsAndEventIdNotExists_WhenPatchEvent_ThenException() {

        UpdateEventRequest request = new UpdateEventRequest();
        request.setEventId(999L);
        request.setAnnotation("this is an updated annotation");

        assertThrows(EventNotFoundException.class,
                () -> eventService.patchEvent(1L, request));
    }

    @Test
    public void givenUserExistsAndEventExists_WhenFindByIds_ThenEventReturned() {

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(1L)
                .description("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        eventService.createEvent(1L, event);

        EventFullDto eventFullDto = eventService.findEventByUserIdAndEventId(1L, 1L);

        assertThat(eventFullDto.getTitle(),
                equalTo("Great event with a lot of interesting staff to look at"));
    }

    @Test
    public void givenUserNotExists_WhenFindByIds_ThenException() {

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(1L)
                .description("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        eventService.createEvent(1L, event);

        assertThrows(UserNotFoundException.class,
                () -> eventService.findEventByUserIdAndEventId(999L, 1L));
    }

    @Test
    public void givenEventNotExists_WhenFindByIds_ThenException() {

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(1L)
                .description("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        eventService.createEvent(1L, event);

        assertThrows(EventNotFoundException.class,
                () -> eventService.findEventByUserIdAndEventId(1L, 999L));
    }

    @Test
    public void givenPendingEventAndIdsAreExisting_WhenCancelByIds_ThenEventCanceled() {

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(1L)
                .description("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        eventService.createEvent(1L, event);

        eventService.cancelEventByUserIdAndEventId(1L, 1L);

        assertThat(em.createQuery("select e from Event e", Event.class)
                .getResultList().get(0).getState(), equalTo(EventState.CANCELED));
    }


    @Test
    public void givenCanceledEventAndIdsAreExisting_WhenCancelByIds_ThenException() {

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(1L)
                .description("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        eventService.createEvent(1L, event);
        eventService.cancelEventByUserIdAndEventId(1L, 1L);

        assertThrows(InvalidParameterException.class,
                () -> eventService.cancelEventByUserIdAndEventId(1L, 1L));
    }


    @Test
    public void givenEventExists_WhenPublish_ThenEventPublished() {

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(1L)
                .description("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        eventService.createEvent(1L, event);

        eventService.publishEvent(1L);

        assertThat(em.createQuery("select e from Event e", Event.class)
                .getSingleResult().getState(), equalTo(EventState.PUBLISHED));
    }

    @Test
    public void givenIdsExisting_WhenFindAllRequestsByIds_ThenListReturned() {

        NewUserRequest user2 = new NewUserRequest("user2@user.com", "user2");
        userService.createUser(user2);

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(1L)
                .description("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        ParticipationRequestDto request = new ParticipationRequestDto();
        request.setRequester(2L);
        request.setEvent(1L);

        eventService.createEvent(1L, event);
        eventService.publishEvent(1L);
        requestService.createRequestFromCurrentUser(2L, 1L);

        List<ParticipationRequestDto> list = eventService.findAllRequestsByUserIdAndEventId(1L, 1L);

        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getEvent(), equalTo(1L));
        assertThat(list.get(0).getRequester(), equalTo(2L));
    }

    @Test
    public void givenIdNotInitiator_WhenFindAllRequestsByIds_ThenException() {

        NewUserRequest newUserRequest2 = new NewUserRequest("user2@user.com", "user2");
        userService.createUser(newUserRequest2);

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(1L)
                .description("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        ParticipationRequestDto request = new ParticipationRequestDto();
        request.setRequester(2L);
        request.setEvent(1L);

        eventService.createEvent(1L, event);
        eventService.publishEvent(1L);
        requestService.createRequestFromCurrentUser(2L, 1L);

        assertThrows(InvalidParameterException.class,
                () -> eventService.findAllRequestsByUserIdAndEventId(2L, 1L));
    }

    @Test
    public void givenPendingEvent_WhenRejectEvent_ThenEventRejected() {

        NewUserRequest newUserRequest2 = new NewUserRequest("user2@user.com", "user2");
        userService.createUser(newUserRequest2);

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(1L)
                .description("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        eventService.createEvent(1L, event);

        assertThat(eventService.rejectEvent(1L).getState(), equalTo(CANCELED));
    }

    @Test
    public void givenValidDto_WhenChangeEvent_ThenEventChanged() {

        NewCategoryDto newCategoryDto2 = new NewCategoryDto("category2");

        NewUserRequest newUserRequest2 = new NewUserRequest("user2@user.com", "user2");

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(1L)
                .description("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        UpdateEventRequest updateEventRequest = UpdateEventRequest.builder()
                .categoryId(2L)
                .eventId(1L)
                .eventDate(LocalDateTime.now().plusHours(10))
                .title("This is updated title of the Event")
                .paid(true)
                .description("This is updated description of the Event")
                .participantLimit(123)
                .annotation("This is an updated annotation of the Event")
                .build();

        categoryService.createCategory(newCategoryDto2);
        userService.createUser(newUserRequest2);
        eventService.createEvent(1L, event);

        EventFullDto dto = eventService.changeEvent(1L, updateEventRequest);

        assertThat(dto.getTitle(), equalTo("This is updated title of the Event"));
        assertThat(dto.getAnnotation(), equalTo("This is an updated annotation of the Event"));
        assertThat(dto.getDescription(), equalTo("This is updated description of the Event"));
        assertThat(dto.getPaid(), equalTo(true));
        assertThat(dto.getParticipantLimit(), equalTo(123));
        assertThat(dto.getCategory().getId(), equalTo(2L));
        assertThat(dto.getId(), equalTo(1L));
    }

    @Test
    public void givenValidEntities_WhenConfirmRequestByIds_ThenRequestConfirmed() {

        NewUserRequest user = new NewUserRequest("user2@user.com", "user2");

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(1L)
                .description("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        ParticipationRequestDto request = new ParticipationRequestDto();
        request.setRequester(2L);
        request.setEvent(1L);

        userService.createUser(user);
        eventService.createEvent(1L, event);
        eventService.publishEvent(1L);
        requestService.createRequestFromCurrentUser(2L, 1L);

        eventService.confirmAnotherRequestToUsersEvent(2L, 1L, 1L);

        assertThat(eventService.confirmAnotherRequestToUsersEvent(2L, 1L, 1L)
                .getStatus(), equalTo(RequestStatus.CONFIRMED));
    }


    @Test
    public void givenValidEntities_WhenRejectRequestByIds_ThenRequestRejected() {

        NewUserRequest user = new NewUserRequest("user2@user.com", "user2");

        NewEventDto event = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(5L))
                .title("Great event with a lot of interesting staff to look at")
                .paid(false)
                .annotation("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting.")
                .category(1L)
                .description("Great event with a lot of interesting staff to look at." +
                        " You will see many interesting. Do not miss it!")
                .location(new Location(12.12f, 23.22f))
                .requestModeration(false)
                .participantLimit(0)
                .build();

        ParticipationRequestDto request = new ParticipationRequestDto();
        request.setRequester(2L);
        request.setEvent(1L);

        userService.createUser(user);
        eventService.createEvent(1L, event);
        eventService.publishEvent(1L);
        requestService.createRequestFromCurrentUser(2L, 1L);

        eventService.rejectAnotherRequestToUsersEvent(2L, 1L, 1L);

        assertThat(eventService.rejectAnotherRequestToUsersEvent(2L, 1L, 1L)
                .getStatus(), equalTo(RequestStatus.REJECTED));
    }
}
