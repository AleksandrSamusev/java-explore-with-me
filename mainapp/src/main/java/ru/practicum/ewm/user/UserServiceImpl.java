package ru.practicum.ewm.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.InvalidParameterException;
import ru.practicum.ewm.exception.UserConflictException;
import ru.practicum.ewm.exception.UserNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        validateNewUserRequest(newUserRequest);
        log.info("New user created");
        User user = userRepository.save(UserMapper.toUserFromNewRequest(newUserRequest));
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.info("User with id = {} not found", userId);
            throw new UserNotFoundException("User not found");
        }
        log.info("delete user id = {}", userId);
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (ids.isEmpty()) {
            log.info("return all users");
            return userRepository.findAll(pageable)
                    .stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());

        }
        log.info("Return users according to ids list");
        return userRepository.findAllUsersByIds(ids, pageable)
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private void validateNewUserRequest(NewUserRequest newUserRequest) {
        if (newUserRequest.getName() == null || newUserRequest.getName().isBlank() || newUserRequest.getEmail() == null) {
            log.info("Mandatory parameter NAME is invalid");
            throw new InvalidParameterException("Invalid parameter");
        }
        if (newUserRequest.getEmail().isBlank() || !newUserRequest.getEmail().contains("@")) {
            log.info("User name - {}. Mandatory parameter EMAIL is invalid", newUserRequest.getName());
            throw new InvalidParameterException("incorrect email address");
        }
        if (isUserExistsByName(newUserRequest.getName())) {
            log.info("User with name - {} already exists", newUserRequest.getName());
            throw new UserConflictException("User with such name already exists");
        }
    }

    private boolean isUserExistsByName(String name) {
        Optional<User> user = Optional.ofNullable(userRepository.findByNameContainingIgnoreCase(name));
        return user.isPresent();
    }


}
