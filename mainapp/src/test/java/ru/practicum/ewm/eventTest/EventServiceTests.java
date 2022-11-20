package ru.practicum.ewm.eventTest;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.ewm.request.Request;
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
import static ru.practicum.ewm.event.EventState.PENDING;

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
    private final RequestService requestService;

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

    @Test
    public void givenEventDateLessThen2hrs_WhenCreateEvent_thenException() {
        NewCategoryDto newCategoryDto = new NewCategoryDto("category");
        categoryService.createCategory(newCategoryDto);
        NewUserRequest newUserRequest = new NewUserRequest("user@user.com", "user");
        userService.createUser(newUserRequest);
        NewEventDto newEventDto = new NewEventDto();
        newEventDto.setEventDate(LocalDateTime.now().plusHours(1L));
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

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, newEventDto));
    }

    @Test
    public void givenEventWithCategoryIsNull_WhenCreateEvent_thenException() {
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
        newEventDto.setCategory(null);
        newEventDto.setDescription("Great event with a lot of interesting staff to look at." +
                " You will see many interesting. Do not miss it!");
        newEventDto.setLocation(new Location(12.12f, 23.22f));
        newEventDto.setRequestModeration(false);
        newEventDto.setParticipantLimit(0);

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, newEventDto));
    }

    @Test
    public void givenEventWithDescriptionIsNull_WhenCreateEvent_thenException() {
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
        newEventDto.setDescription(null);
        newEventDto.setLocation(new Location(12.12f, 23.22f));
        newEventDto.setRequestModeration(false);
        newEventDto.setParticipantLimit(0);

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, newEventDto));
    }

    @Test
    public void givenEventWithDescriptionIsBlank_WhenCreateEvent_thenException() {
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
        newEventDto.setDescription("               ");
        newEventDto.setLocation(new Location(12.12f, 23.22f));
        newEventDto.setRequestModeration(false);
        newEventDto.setParticipantLimit(0);

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, newEventDto));
    }

    @Test
    public void givenEventWithDescriptionLessThen20_WhenCreateEvent_thenException() {
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
        newEventDto.setDescription("Do not miss it!");
        newEventDto.setLocation(new Location(12.12f, 23.22f));
        newEventDto.setRequestModeration(false);
        newEventDto.setParticipantLimit(0);

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, newEventDto));
    }

    @Test
    public void givenEventWithDescriptionMoreThen7000_WhenCreateEvent_thenException() {
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
        newEventDto.setDescription(
                "1234567891123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
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
                        "");
        newEventDto.setLocation(new Location(12.12f, 23.22f));
        newEventDto.setRequestModeration(false);
        newEventDto.setParticipantLimit(0);

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, newEventDto));
    }

    @Test
    public void givenEventWithEventDateInPast_WhenCreateEvent_thenException() {
        NewCategoryDto newCategoryDto = new NewCategoryDto("category");
        categoryService.createCategory(newCategoryDto);
        NewUserRequest newUserRequest = new NewUserRequest("user@user.com", "user");
        userService.createUser(newUserRequest);
        NewEventDto newEventDto = new NewEventDto();
        newEventDto.setEventDate(LocalDateTime.now().minusHours(2L));
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

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, newEventDto));
    }

    @Test
    public void givenEventWithLatIsNull_WhenCreateEvent_thenException() {
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
        newEventDto.setLocation(new Location(null, 23.22f));
        newEventDto.setRequestModeration(false);
        newEventDto.setParticipantLimit(0);

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, newEventDto));
    }

    @Test
    public void givenEventWithLonIsNull_WhenCreateEvent_thenException() {
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
        newEventDto.setLocation(new Location(23.22f, null));
        newEventDto.setRequestModeration(false);
        newEventDto.setParticipantLimit(0);

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, newEventDto));
    }

    @Test
    public void givenEventWithLocationIsNull_WhenCreateEvent_thenException() {
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
        newEventDto.setLocation(null);
        newEventDto.setRequestModeration(false);
        newEventDto.setParticipantLimit(0);

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, newEventDto));
    }

    @Test
    public void givenEventWithTitleIsNull_WhenCreateEvent_thenException() {
        NewCategoryDto newCategoryDto = new NewCategoryDto("category");
        categoryService.createCategory(newCategoryDto);
        NewUserRequest newUserRequest = new NewUserRequest("user@user.com", "user");
        userService.createUser(newUserRequest);
        NewEventDto newEventDto = new NewEventDto();
        newEventDto.setEventDate(LocalDateTime.now().plusHours(5L));
        newEventDto.setTitle(null);
        newEventDto.setPaid(false);
        newEventDto.setAnnotation("Great event with a lot of interesting staff to look at." +
                " You will see many interesting.");
        newEventDto.setCategory(1L);
        newEventDto.setDescription("Great event with a lot of interesting staff to look at." +
                " You will see many interesting. Do not miss it!");
        newEventDto.setLocation(new Location(23.22f, 23.22f));
        newEventDto.setRequestModeration(false);
        newEventDto.setParticipantLimit(0);

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, newEventDto));
    }

    @Test
    public void givenEventWithTitleIsBlank_WhenCreateEvent_thenException() {
        NewCategoryDto newCategoryDto = new NewCategoryDto("category");
        categoryService.createCategory(newCategoryDto);
        NewUserRequest newUserRequest = new NewUserRequest("user@user.com", "user");
        userService.createUser(newUserRequest);
        NewEventDto newEventDto = new NewEventDto();
        newEventDto.setEventDate(LocalDateTime.now().plusHours(5L));
        newEventDto.setTitle("                     ");
        newEventDto.setPaid(false);
        newEventDto.setAnnotation("Great event with a lot of interesting staff to look at." +
                " You will see many interesting.");
        newEventDto.setCategory(1L);
        newEventDto.setDescription("Great event with a lot of interesting staff to look at." +
                " You will see many interesting. Do not miss it!");
        newEventDto.setLocation(new Location(23.22f, 23.22f));
        newEventDto.setRequestModeration(false);
        newEventDto.setParticipantLimit(0);

        assertThrows(InvalidParameterException.class,
                () -> eventService.createEvent(1L, newEventDto));
    }

    @Test
    public void givenUserExistsAndEventExists_WhenGetUsersEvents_ThenReturnEventList() {
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

        assertThat(eventService.findAllUsersEvents(1L, 0, 10)
                .size(), equalTo(1));
        assertThat(eventService.findAllUsersEvents(1L, 0, 10)
                .get(0).getInitiator().getName(), equalTo("user"));
    }

    @Test
    public void givenUserNotExists_WhenGetUsersEvents_ThenException() {
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

        assertThrows(UserNotFoundException.class,
                () -> eventService.findAllUsersEvents(999L, 0, 10));
    }

    @Test
    public void givenUserExistsAndValidRequest_WhenPatchEvent_ThenEventPatched() {

        NewCategoryDto newCategoryDto = new NewCategoryDto("category");
        categoryService.createCategory(newCategoryDto);

        NewCategoryDto newCategoryDto2 = new NewCategoryDto("category2");
        categoryService.createCategory(newCategoryDto2);


        NewUserRequest newUserRequest = new NewUserRequest("user@user.com", "user");
        userService.createUser(newUserRequest);

        NewEventDto newEventDto = new NewEventDto();
        newEventDto.setEventDate(LocalDateTime.now().plusHours(5L));
        newEventDto.setTitle("Great event with a lot of interesting staff to look at");
        newEventDto.setPaid(false);
        newEventDto.setAnnotation("Great event with a lot of interesting staff to look at.");
        newEventDto.setCategory(1L);
        newEventDto.setDescription("Great event with a lot of interesting staff!");
        newEventDto.setLocation(new Location(12.12f, 23.22f));
        newEventDto.setRequestModeration(false);
        newEventDto.setParticipantLimit(0);
        eventService.createEvent(1L, newEventDto);

        UpdateEventRequest updateEventRequest = new UpdateEventRequest();
        updateEventRequest.setEventDate(LocalDateTime.now().plusHours(8L));
        updateEventRequest.setEventId(1L);
        updateEventRequest.setPaid(true);
        updateEventRequest.setDescription("This description was updated");
        updateEventRequest.setAnnotation("this is an updated annotation");
        updateEventRequest.setParticipantLimit(999);
        updateEventRequest.setCategoryId(2L);

        assertThat(eventService.patchEvent(1L, updateEventRequest).getId(), equalTo(1L));
        assertThat(eventService.patchEvent(1L, updateEventRequest).getAnnotation(),
                equalTo("this is an updated annotation"));
        assertThat(eventService.patchEvent(1L, updateEventRequest).getTitle(),
                equalTo("Great event with a lot of interesting staff to look at"));
        assertThat(eventService.patchEvent(1L, updateEventRequest).getDescription(),
                equalTo("This description was updated"));
        assertThat(eventService.patchEvent(1L, updateEventRequest).getPaid(),
                equalTo(true));
        assertThat(eventService.patchEvent(1L, updateEventRequest).getParticipantLimit(),
                equalTo(999));
        assertThat(eventService.patchEvent(1L, updateEventRequest).getCategory().getId(),
                equalTo(2L));


        Event event = em.createQuery("SELECT e from Event e where e.id = 1",
                Event.class).getSingleResult();
        assertThat(event.getAnnotation(), equalTo("this is an updated annotation"));
        assertThat(event.getDescription(), equalTo("This description was updated"));
    }

    @Test
    public void givenUserExistsAndEventIdNotExists_WhenPatchEvent_ThenException() {

        NewCategoryDto newCategoryDto = new NewCategoryDto("category");
        categoryService.createCategory(newCategoryDto);

        NewUserRequest newUserRequest = new NewUserRequest("user@user.com", "user");
        userService.createUser(newUserRequest);

        NewEventDto newEventDto = new NewEventDto();
        newEventDto.setEventDate(LocalDateTime.now().plusHours(5L));
        newEventDto.setTitle("Great event with a lot of interesting staff to look at");
        newEventDto.setPaid(false);
        newEventDto.setAnnotation("Great event with a lot of interesting staff to look at.");
        newEventDto.setCategory(1L);
        newEventDto.setDescription("Great event with a lot of interesting staff!");
        newEventDto.setLocation(new Location(12.12f, 23.22f));
        newEventDto.setRequestModeration(false);
        newEventDto.setParticipantLimit(0);
        eventService.createEvent(1L, newEventDto);

        UpdateEventRequest updateEventRequest = new UpdateEventRequest();
        updateEventRequest.setEventId(999L);
        updateEventRequest.setAnnotation("this is an updated annotation");

        assertThrows(EventNotFoundException.class,
                () -> eventService.patchEvent(1L, updateEventRequest));
    }

    @Test
    public void givenUserExistsAndEventExists_WhenFindByIds_ThenEventReturned() {
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

        Event event = (Event) em.createQuery("select e from Event e where e.id = 1 and e.initiator.id = 1")
                .getSingleResult();
        assertThat(event.getTitle(), equalTo("Great event with a lot of interesting staff to look at"));
        assertThat(eventService.findEventByUserIdAndEventId(1L, 1L).getTitle(),
                equalTo("Great event with a lot of interesting staff to look at"));
    }

    @Test
    public void givenUserNotExists_WhenFindByIds_ThenException() {
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

        assertThrows(UserNotFoundException.class,
                () -> eventService.findEventByUserIdAndEventId(999L, 1L));
    }

    @Test
    public void givenEventNotExists_WhenFindByIds_ThenException() {
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

        assertThrows(EventNotFoundException.class,
                () -> eventService.findEventByUserIdAndEventId(1L, 999L));
    }

    @Test
    public void givenPendingEventAndIdsAreExisting_WhenCancelByIds_ThenEventCanceled() {
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

        List<Event> events = em.createQuery("select e from Event e ", Event.class)
                .getResultList();
        assertThat(events.size(), equalTo(1));
        assertThat(events.get(0).getId(), equalTo(1L));
        assertThat(events.get(0).getTitle(),
                equalTo("Great event with a lot of interesting staff to look at"));
        assertThat(events.get(0).getState(), equalTo(PENDING));

        eventService.cancelEventByUserIdAndEventId(1L, 1L);

        List<Event> eventsAfter = em.createQuery("select e from Event e", Event.class).getResultList();
        assertThat(events.size(), equalTo(1));
        assertThat(events.get(0).getId(), equalTo(1L));
        assertThat(events.get(0).getTitle(),
                equalTo("Great event with a lot of interesting staff to look at"));
        assertThat(events.get(0).getState(), equalTo(EventState.CANCELED));
    }


    @Test
    public void givenCanceledEventAndIdsAreExisting_WhenCancelByIds_ThenException() {
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

        List<Event> events = em.createQuery("select e from Event e ", Event.class)
                .getResultList();
        assertThat(events.get(0).getId(), equalTo(1L));
        assertThat(events.get(0).getState(), equalTo(PENDING));

        eventService.cancelEventByUserIdAndEventId(1L, 1L);

        List<Event> eventsAfter = em.createQuery("select e from Event e ", Event.class)
                .getResultList();
        assertThat(eventsAfter.get(0).getId(), equalTo(1L));
        assertThat(eventsAfter.get(0).getState(), equalTo(EventState.CANCELED));

        assertThrows(InvalidParameterException.class,
                () -> eventService.cancelEventByUserIdAndEventId(1L, 1L));
    }


    @Test
    public void givenEventExists_WhenPublish_ThenEventPublished() {
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

        eventService.publishEvent(1L);

        Event event = em.createQuery("select e from Event e", Event.class).getSingleResult();
        assertThat(event.getState(), equalTo(EventState.PUBLISHED));
    }

    @Test
    public void givenIdsExisting_WhenFindAllRequestsByIds_ThenListReturned() {
        NewCategoryDto newCategoryDto = new NewCategoryDto("category");
        categoryService.createCategory(newCategoryDto);

        NewUserRequest newUserRequest = new NewUserRequest("user@user.com", "user");
        userService.createUser(newUserRequest);
        NewUserRequest newUserRequest2 = new NewUserRequest("user2@user.com", "user2");
        userService.createUser(newUserRequest2);

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

        eventService.publishEvent(1L);

        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setRequester(2L);
        participationRequestDto.setEvent(1L);
        requestService.createRequestFromCurrentUser(2L, 1L);

        List<ParticipationRequestDto> list = eventService.findAllRequestsByUserIdAndEventId(1L, 1L);

        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getEvent(), equalTo(1L));
        assertThat(list.get(0).getRequester(), equalTo(2L));
    }

    @Test
    public void givenIdNotInitiator_WhenFindAllRequestsByIds_ThenException() {
        NewCategoryDto newCategoryDto = new NewCategoryDto("category");
        categoryService.createCategory(newCategoryDto);

        NewUserRequest newUserRequest = new NewUserRequest("user@user.com", "user");
        userService.createUser(newUserRequest);
        NewUserRequest newUserRequest2 = new NewUserRequest("user2@user.com", "user2");
        userService.createUser(newUserRequest2);

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

        eventService.publishEvent(1L);

        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setRequester(2L);
        participationRequestDto.setEvent(1L);
        requestService.createRequestFromCurrentUser(2L, 1L);

        assertThrows(InvalidParameterException.class,
                () -> eventService.findAllRequestsByUserIdAndEventId(2L, 1L));
    }

    @Test
    public void givenPendingEvent_WhenRejectEvent_ThenEventRejected() {
        NewCategoryDto newCategoryDto = new NewCategoryDto("category");
        categoryService.createCategory(newCategoryDto);

        NewUserRequest newUserRequest = new NewUserRequest("user@user.com", "user");
        userService.createUser(newUserRequest);
        NewUserRequest newUserRequest2 = new NewUserRequest("user2@user.com", "user2");
        userService.createUser(newUserRequest2);

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

        List<Event> events = em.createQuery("select e from Event e ", Event.class).getResultList();
        assertThat(events.get(0).getState(), equalTo(PENDING));

        eventService.rejectEvent(1L);

        List<Event> eventsAfter = em.createQuery("select e from Event e ", Event.class).getResultList();
        assertThat(eventsAfter.get(0).getState(), equalTo(CANCELED));
    }

    @Test
    public void givenValidDto_WhenChangeEvent_ThenEventChanged() {
        NewCategoryDto newCategoryDto = new NewCategoryDto("category");
        categoryService.createCategory(newCategoryDto);

        NewCategoryDto newCategoryDto2 = new NewCategoryDto("category2");
        categoryService.createCategory(newCategoryDto2);

        NewUserRequest newUserRequest = new NewUserRequest("user@user.com", "user");
        userService.createUser(newUserRequest);
        NewUserRequest newUserRequest2 = new NewUserRequest("user2@user.com", "user2");
        userService.createUser(newUserRequest2);

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

        UpdateEventRequest updateEventRequest = new UpdateEventRequest();
        updateEventRequest.setCategoryId(2L);
        updateEventRequest.setEventId(1L);
        updateEventRequest.setEventDate(LocalDateTime.now().plusHours(10));
        updateEventRequest.setTitle("This is updated title of the Event");
        updateEventRequest.setPaid(true);
        updateEventRequest.setDescription("This is updated description of the Event");
        updateEventRequest.setParticipantLimit(123);
        updateEventRequest.setAnnotation("This is an updated annotation of the Event");

        EventFullDto eventFullDto = eventService.changeEvent(1L, updateEventRequest);
        assertThat(eventFullDto.getTitle(), equalTo("This is updated title of the Event"));
        assertThat(eventFullDto.getAnnotation(), equalTo("This is an updated annotation of the Event"));
        assertThat(eventFullDto.getDescription(), equalTo("This is updated description of the Event"));
        assertThat(eventFullDto.getPaid(), equalTo(true));
        assertThat(eventFullDto.getParticipantLimit(), equalTo(123));
        assertThat(eventFullDto.getCategory().getId(), equalTo(2L));
        assertThat(eventFullDto.getId(), equalTo(1L));
    }

    @Test
    public void givenValidEntities_WhenConfirmRequestByIds_ThenRequestConfirmed() {
        NewCategoryDto newCategoryDto = new NewCategoryDto("category");
        categoryService.createCategory(newCategoryDto);

        NewUserRequest newUserRequest = new NewUserRequest("user@user.com", "user");
        userService.createUser(newUserRequest);

        NewUserRequest newUserRequest2 = new NewUserRequest("user2@user.com", "user2");
        userService.createUser(newUserRequest2);

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
        newEventDto.setRequestModeration(true);
        newEventDto.setParticipantLimit(0);
        eventService.createEvent(1L, newEventDto);
        assertThat(eventRepository.getReferenceById(1L).getTitle(),
                equalTo("Great event with a lot of interesting staff to look at"));
        List<Event> event = em.createQuery("select e from Event e", Event.class).getResultList();
        assertThat(event.size(), equalTo(1));

        eventService.publishEvent(1L);

        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setRequester(2L);
        participationRequestDto.setEvent(1L);
        requestService.createRequestFromCurrentUser(2L, 1L);

        List<Request> requests =
                em.createQuery("select r from Request r", Request.class).getResultList();

        assertThat(requests.size(), equalTo(1));
        assertThat(requests.get(0).getRequesterId(), equalTo(2L));
        assertThat(requests.get(0).getStatus(), equalTo(RequestStatus.PENDING));

        eventService.confirmAnotherRequestToUsersEvent(2L, 1L, 1L);

        List<Request> requestsAfter =
                em.createQuery("select r from Request r", Request.class).getResultList();
        assertThat(requestsAfter.get(0).getStatus(), equalTo(RequestStatus.CONFIRMED));
    }


    @Test
    public void givenValidEntities_WhenRejectRequestByIds_ThenRequestRejected() {
        NewCategoryDto newCategoryDto = new NewCategoryDto("category");
        categoryService.createCategory(newCategoryDto);

        NewUserRequest newUserRequest = new NewUserRequest("user@user.com", "user");
        userService.createUser(newUserRequest);

        NewUserRequest newUserRequest2 = new NewUserRequest("user2@user.com", "user2");
        userService.createUser(newUserRequest2);

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
        newEventDto.setRequestModeration(true);
        newEventDto.setParticipantLimit(0);
        eventService.createEvent(1L, newEventDto);
        assertThat(eventRepository.getReferenceById(1L).getTitle(),
                equalTo("Great event with a lot of interesting staff to look at"));
        List<Event> event = em.createQuery("select e from Event e", Event.class).getResultList();
        assertThat(event.size(), equalTo(1));

        eventService.publishEvent(1L);

        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setRequester(2L);
        participationRequestDto.setEvent(1L);
        requestService.createRequestFromCurrentUser(2L, 1L);

        List<Request> requests =
                em.createQuery("select r from Request r", Request.class).getResultList();

        assertThat(requests.size(), equalTo(1));
        assertThat(requests.get(0).getRequesterId(), equalTo(2L));
        assertThat(requests.get(0).getStatus(), equalTo(RequestStatus.PENDING));

        eventService.rejectAnotherRequestToUsersEvent(2L, 1L, 1L);

        List<Request> requestsAfter =
                em.createQuery("select r from Request r", Request.class).getResultList();
        assertThat(requestsAfter.get(0).getStatus(), equalTo(RequestStatus.REJECTED));
    }

}
