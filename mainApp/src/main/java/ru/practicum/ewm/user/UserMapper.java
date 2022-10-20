package ru.practicum.ewm.user;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {
    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setUserId(user.getUserId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public static User toUser(UserDto userDto) {
        User user = new User();
        user.setUserId(userDto.getUserId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public static UserShortDto toUserShortDto(User user) {
        UserShortDto userShortDto = new UserShortDto();
        userShortDto.setUserId(user.getUserId());
        userShortDto.setName(user.getName());
        return userShortDto;
    }

    public static User toUserFromShortDto(UserShortDto userShortDto) {
        User user = new User();
        user.setUserId(userShortDto.getUserId());
        user.setName(userShortDto.getName());
        return user;
    }

    public static User toUserFromNewRequest(NewUserRequest newUserRequest) {
        User user = new User();
        user.setName(newUserRequest.getName());
        user.setEmail(newUserRequest.getEmail());
        return user;
    }

    public static List<UserDto> toUserDtoList(List<User> users) {
        List<UserDto> dtos = new ArrayList<>();
        for (User user : users) {
            dtos.add(toUserDto(user));
        }
        return dtos;
    }


}
