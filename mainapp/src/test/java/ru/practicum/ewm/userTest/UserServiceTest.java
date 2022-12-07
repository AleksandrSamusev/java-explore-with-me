package ru.practicum.ewm.userTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.InvalidParameterException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.NewUserRequest;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserDto;
import ru.practicum.ewm.user.UserService;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
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
public class UserServiceTest<T extends UserService> {

    private final EntityManager em;
    private final UserService userService;


    @Test
    public void givenValidUser_WhenCreateUser_ThenUserCreated() {

        NewUserRequest newUserRequest = new NewUserRequest("User@user.com", "User");
        userService.createUser(newUserRequest);

        assertThat(em.createQuery("SELECT u from User u where u.name = 'User'", User.class)
                .getSingleResult().getEmail(), equalTo("User@user.com"));
    }

    @Test
    public void givenNameEqualsNull_WhenCreateUser_ThenException() {

        NewUserRequest newUserRequest = new NewUserRequest("User@user.com", null);

        assertThrows(InvalidParameterException.class,
                () -> userService.createUser(newUserRequest));
    }

    @Test
    public void givenNameIsBlank_WhenCreateUser_ThenException() {

        NewUserRequest newUserRequest = new NewUserRequest("User@user.com", "  ");

        assertThrows(InvalidParameterException.class,
                () -> userService.createUser(newUserRequest));
    }

    @Test
    public void givenEmailIsNull_WhenCreateUser_ThenException() {

        NewUserRequest newUserRequest = new NewUserRequest(null, "User");

        assertThrows(InvalidParameterException.class,
                () -> userService.createUser(newUserRequest));
    }

    @Test
    public void givenEmailIsBlank_WhenCreateUser_ThenException() {

        NewUserRequest newUserRequest = new NewUserRequest("  ", "User");

        assertThrows(InvalidParameterException.class,
                () -> userService.createUser(newUserRequest));
    }

    @Test
    public void givenNameEqualsToExistingUserName_WhenCreateUser_ThenException() {

        NewUserRequest user1 = new NewUserRequest("user@user.com", "User");
        NewUserRequest user2 = new NewUserRequest("user2@user.com", "User");
        userService.createUser(user1);

        assertThrows(ConflictException.class,
                () -> userService.createUser(user2));
    }

    @Test
    public void givenUserExists_WhenDeleteUser_ThenUserDeleted() {

        NewUserRequest user = new NewUserRequest("User@user.com", "User");
        userService.createUser(user);

        userService.deleteUserById(1L);

        assertThat(em.createQuery("select u from User u", User.class)
                .getResultList().size(), equalTo(0));
    }

    @Test
    public void givenUserNotExists_WhenDeleteUser_ThenException() {

        NewUserRequest user = new NewUserRequest("User@user.com", "User");
        userService.createUser(user);

        assertThrows(NotFoundException.class,
                () -> userService.deleteUserById(999L));
    }

    @Test
    public void givenThreeIdsOfCreatedUsers_WhenGetUsers_ThenReturnListOfThree() {

        NewUserRequest newUserRequest1 = new NewUserRequest("user1@mail.com", "user1");
        NewUserRequest newUserRequest2 = new NewUserRequest("user2@mail.com", "user2");
        NewUserRequest newUserRequest3 = new NewUserRequest("user3@mail.com", "user3");
        NewUserRequest newUserRequest4 = new NewUserRequest("user4@mail.com", "user4");
        NewUserRequest newUserRequest5 = new NewUserRequest("user5@mail.com", "user5");
        userService.createUser(newUserRequest1);
        userService.createUser(newUserRequest2);
        userService.createUser(newUserRequest3);
        userService.createUser(newUserRequest4);
        userService.createUser(newUserRequest5);

        List<UserDto> listOfThreeUsers = userService.getUsers(List.of(1L, 2L, 3L), 0, 10);

        assertThat(listOfThreeUsers.size(), equalTo(3));
        assertThat(listOfThreeUsers.get(0).getId(), equalTo(1L));
        assertThat(listOfThreeUsers.get(1).getId(), equalTo(2L));
        assertThat(listOfThreeUsers.get(2).getId(), equalTo(3L));
    }

    @Test
    public void givenIdsIsEmptyAndFiveCreatedUsers_WhenGetUsers_ThenReturnListOfFive() {

        NewUserRequest newUserRequest1 = new NewUserRequest("user1@mail.com", "user1");
        NewUserRequest newUserRequest2 = new NewUserRequest("user2@mail.com", "user2");
        NewUserRequest newUserRequest3 = new NewUserRequest("user3@mail.com", "user3");
        NewUserRequest newUserRequest4 = new NewUserRequest("user4@mail.com", "user4");
        NewUserRequest newUserRequest5 = new NewUserRequest("user5@mail.com", "user5");
        userService.createUser(newUserRequest1);
        userService.createUser(newUserRequest2);
        userService.createUser(newUserRequest3);
        userService.createUser(newUserRequest4);
        userService.createUser(newUserRequest5);

        List<UserDto> listOfThreeUsers = userService.getUsers(List.of(), 0, 10);

        assertThat(listOfThreeUsers.size(), equalTo(5));
        assertThat(listOfThreeUsers.get(0).getId(), equalTo(1L));
        assertThat(listOfThreeUsers.get(1).getId(), equalTo(2L));
        assertThat(listOfThreeUsers.get(2).getId(), equalTo(3L));
        assertThat(listOfThreeUsers.get(3).getId(), equalTo(4L));
        assertThat(listOfThreeUsers.get(4).getId(), equalTo(5L));
    }

}
