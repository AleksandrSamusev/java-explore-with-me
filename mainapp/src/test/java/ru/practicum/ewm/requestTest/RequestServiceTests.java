package ru.practicum.ewm.requestTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
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
import ru.practicum.ewm.request.Request;
import ru.practicum.ewm.request.RequestService;
import ru.practicum.ewm.request.RequestStatus;
import ru.practicum.ewm.user.NewUserRequest;
import ru.practicum.ewm.user.UserService;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

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

    @BeforeEach
    public void initData() {

        NewCategoryDto newCategoryDto = new NewCategoryDto("category");

        NewUserRequest newUserRequest = new NewUserRequest("user@user.com", "user");

        NewUserRequest newUserRequest2 = new NewUserRequest("user2@user.com", "user2");

        NewEventDto newEventDto = NewEventDto.builder()
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
                .participantLimit(0).build();

        categoryService.createCategory(newCategoryDto);
        userService.createUser(newUserRequest);
        userService.createUser(newUserRequest2);
        eventService.createEvent(1L, newEventDto);
        eventService.publishEvent(1L);
        requestService.createRequestFromCurrentUser(2L, 1L);
    }

    @Test
    public void givenIdsExist_WhenCreateRequest_ThenRequestCreated() {

        assertThat(em.createQuery("select r from Request r", Request.class)
                .getResultList().get(0).getRequesterId(), equalTo(2L));
    }

    @Test
    public void givenIdsExist_WhenCCancelOwnRequest_ThenRequestCanceled() {

        requestService.cancelOwnRequest(2L, 1L);

        assertThat(em.createQuery("select r from Request r", Request.class)
                .getResultList().get(0).getStatus(), equalTo(RequestStatus.CANCELED));
    }

    @Test
    public void givenUserIdNotExist_WhenCancelOwnRequest_ThenException() {

        requestService.cancelOwnRequest(2L, 1L);

        assertThrows(UserNotFoundException.class,
                () -> requestService.cancelOwnRequest(999L, 1L));
    }

    @Test
    public void givenRequestIdNotExist_WhenCancelOwnRequest_ThenException() {

        assertThrows(RequestNotFoundException.class,
                () -> requestService.cancelOwnRequest(2L, 9999L));
    }

}
