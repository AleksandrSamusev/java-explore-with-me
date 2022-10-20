package ru.practicum.ewm.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.UserNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto createUser(NewUserRequest newUserRequest) {
        log.info("Created user");
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUserFromNewRequest(newUserRequest)));
    }

    public void deleteUserById(Long userId) {
        log.info("delete user id = {}", userId);
        userRepository.deleteById(userId);
    }

    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        List<User> users = new ArrayList<>();
        if (ids != null) {
            validateIds(ids);
            for (Long id : ids) {
                users.add(userRepository.getReferenceById(id));
            }
        } else {
            Pageable pageable = PageRequest.of(from / size, size, Sort.by("userId"));
            users = userRepository.findAllUsers(pageable);
        }
        return UserMapper.toUserDtoList(users);
    }

    private void validateIds(List<Long> ids) {
        for (Long id : ids) {
            if (!userRepository.existsById(id)) {
                throw new UserNotFoundException("User not found");
            }
        }
    }


}
