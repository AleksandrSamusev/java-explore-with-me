package ru.practicum.ewm.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.UserNotFoundException;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        log.info("Created user");
        return userRepository.save(user);
    }

    public void deleteUserById(Long userId) {
        log.info("delete user id = {}", userId);
        userRepository.deleteById(userId);
    }

    public List<User> getUsers(List<Long> ids, Integer from, Integer size) {
        validateIds(ids);

        return null;
    }

    private void validateIds(List<Long> ids) {
        for (Long id : ids) {
            if (!userRepository.existsById(id)) {
                throw new UserNotFoundException("User not found");
            }
        }
    }


}
