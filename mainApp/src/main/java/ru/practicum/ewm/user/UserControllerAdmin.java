package ru.practicum.ewm.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin/users")
public class UserControllerAdmin {
    private final UserServiceImpl userService;

    @Autowired
    public UserControllerAdmin(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createUser(@RequestBody NewUserRequest newUserRequest) {
        return userService.createUser(newUserRequest);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUserById(userId);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @RequestParam(required = false, defaultValue = "0") Integer from,
                                  @RequestParam(required = false, defaultValue = "10") Integer size) {
        return userService.getUsers(ids, from, size);
    }

}
