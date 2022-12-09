package ru.practicum.ewm.user;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setInitiatorRating(user.getInitiatorRating());
        userDto.setReviewerRating(user.getReviewerRating());
        return userDto;
    }

    public static UserShortDto toUserShortDto(User user) {
        UserShortDto userShortDto = new UserShortDto();
        userShortDto.setId(user.getId());
        userShortDto.setName(user.getName());
        userShortDto.setInitiatorRating(user.getInitiatorRating());
        userShortDto.setReviewerRating(user.getReviewerRating());
        return userShortDto;
    }

    public static User toUserFromNewRequest(NewUserRequest newUserRequest) {
        User user = new User();
        user.setName(newUserRequest.getName());
        user.setEmail(newUserRequest.getEmail());
        return user;
    }
}
