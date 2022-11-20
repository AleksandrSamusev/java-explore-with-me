package ru.practicum.ewm.userTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.ewm.exception.InvalidParameterException;
import ru.practicum.ewm.exception.UserConflictException;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.user.NewUserRequest;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserDto;
import ru.practicum.ewm.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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
    public void givenNameIsOkAndEmailIsOk_WhenCreateUser_ThenUserCreated() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("User");
        newUserRequest.setEmail("User@user.com");
        userService.createUser(newUserRequest);
        TypedQuery<User> query = em.createQuery("SELECT u from User u where u.name = :name", User.class);
        User dbUser = query.setParameter("name", newUserRequest.getName()).getSingleResult();
        assertThat(dbUser.getEmail(), equalTo("User@user.com"));
    }

    @Test
    public void givenNameEqualsNullAndEmailIsOk_WhenCreateUser_ThenException() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName(null);
        newUserRequest.setEmail("User@user.com");
        assertThrows(InvalidParameterException.class,
                () -> userService.createUser(newUserRequest));
    }

    @Test
    public void givenNameEqualsEmptyAndEmailIsOk_WhenCreateUser_ThenException() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("");
        newUserRequest.setEmail("User@user.com");
        assertThrows(InvalidParameterException.class,
                () -> userService.createUser(newUserRequest));
    }

    @Test
    public void givenNameIsOkAndEmailIsNull_WhenCreateUser_ThenException() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("User");
        newUserRequest.setEmail(null);
        assertThrows(InvalidParameterException.class,
                () -> userService.createUser(newUserRequest));
    }

    @Test
    public void givenNameIsOkAndEmailIsEmpty_WhenCreateUser_ThenException() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("User");
        newUserRequest.setEmail("");
        assertThrows(InvalidParameterException.class,
                () -> userService.createUser(newUserRequest));
    }

    @Test
    public void givenNameIsEqualToExistingUserNameAndEmailIsOk_WhenCreateUser_ThenException() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("User");
        newUserRequest.setEmail("user@user.com");
        userService.createUser(newUserRequest);

        NewUserRequest newUserRequest2 = new NewUserRequest();
        newUserRequest2.setName("User");
        newUserRequest.setEmail("user2@user.com");

        assertThrows(UserConflictException.class,
                () -> userService.createUser(newUserRequest));
    }

    @Test
    public void givenIdIsInDatabase_WhenDeleteUser_ThenUserDeleted() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("User");
        newUserRequest.setEmail("User@user.com");
        userService.createUser(newUserRequest);
        List<User> users = em.createQuery("select u from User u", User.class).getResultList();
        assertThat(users.size(), equalTo(1));
        assertThat(users.get(0).getId(), equalTo(1L));
        userService.deleteUserById(1L);
        List<User> usersAfter = em.createQuery("select u from User u", User.class).getResultList();
        assertThat(usersAfter.size(), equalTo(0));
    }

    @Test
    public void givenIdIsNotInDatabase_WhenDeleteUser_ThenException() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("User");
        newUserRequest.setEmail("User@user.com");
        userService.createUser(newUserRequest);
        List<User> users = em.createQuery("select u from User u", User.class).getResultList();
        assertThat(users.size(), equalTo(1));
        assertThat(users.get(0).getId(), equalTo(1L));
        assertThrows(UserNotFoundException.class,
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
