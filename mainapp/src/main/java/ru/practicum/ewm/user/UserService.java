package ru.practicum.ewm.user;

import java.util.List;

public interface UserService {

    UserDto createUser(NewUserRequest newUserRequest);

    void deleteUserById(Long userId);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);
}
