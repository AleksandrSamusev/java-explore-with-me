package ru.practicum.ewm.requestTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.ewm.category.CategoryService;
import ru.practicum.ewm.category.NewCategoryDto;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.event.NewEventDto;
import ru.practicum.ewm.exception.RequestNotFoundException;
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

@Transactional
@SpringBootTest(
        properties = "db.name=ewm",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RequestServiceTests<T extends RequestService> {

    private final CategoryService categoryService;
    private final UserService userService;
    private final EventService eventService;
    private final RequestService requestService;
    private final EntityManager em;

    @Test
    public void givenIdsExist_WhenCreateRequest_ThenRequestCreated() {
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

        List<Request> requests = em.createQuery("select r from Request r", Request.class).getResultList();
        assertThat(requests.size(), equalTo(1));
        assertThat(requests.get(0).getRequesterId(), equalTo(2L));
    }

    @Test
    public void givenIdsExist_WhenCCancelOwnRequest_ThenRequestCanceled() {
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

        List<Request> requests = em.createQuery("select r from Request r", Request.class).getResultList();
        assertThat(requests.size(), equalTo(1));
        assertThat(requests.get(0).getRequesterId(), equalTo(2L));

        requestService.cancelOwnRequest(2L, 1L);
        List<Request> requestsAfter = em.createQuery("select r from Request r", Request.class).getResultList();
        assertThat(requestsAfter.get(0).getStatus(), equalTo(RequestStatus.CANCELED));
    }

    @Test
    public void givenUserIdNotExist_WhenCCancelOwnRequest_ThenException() {
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

        List<Request> requests = em.createQuery("select r from Request r", Request.class).getResultList();
        assertThat(requests.size(), equalTo(1));
        assertThat(requests.get(0).getRequesterId(), equalTo(2L));
        assertThrows(UserNotFoundException.class,
                () -> requestService.cancelOwnRequest(999L, 1L));
    }

    @Test
    public void givenRequestIdNotExist_WhenCCancelOwnRequest_ThenException() {
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

        List<Request> requests = em.createQuery("select r from Request r", Request.class).getResultList();
        assertThat(requests.size(), equalTo(1));
        assertThat(requests.get(0).getRequesterId(), equalTo(2L));
        assertThrows(RequestNotFoundException.class,
                () -> requestService.cancelOwnRequest(2L, 9999L));
    }

}
