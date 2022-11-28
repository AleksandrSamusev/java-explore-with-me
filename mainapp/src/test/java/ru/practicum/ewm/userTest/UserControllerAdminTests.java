package ru.practicum.ewm.userTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.user.UserControllerAdmin;
import ru.practicum.ewm.user.UserDto;
import ru.practicum.ewm.user.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserControllerAdmin.class)
public class UserControllerAdminTests {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private MockMvc mvc;
    private final UserDto userDto = new UserDto(1L, "John", "john@joseph.com", 0, null);

    @Test
    public void createNewUserTest() throws Exception {
        when(userService.createUser(any()))
                .thenReturn(userDto);

        mvc.perform(post("/admin/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@joseph.com"));
    }

    @Test
    public void deleteUserByIdTest() throws Exception {
        mvc.perform(delete("/admin/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllUsers() throws Exception {

        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setName("adam");
        user1.setEmail("adam@adam.com");

        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setName("adam2");
        user2.setEmail("adam2@adam.com");

        ArrayList<UserDto> list = new ArrayList<>();
        list.add(user1);
        list.add(user2);

        Mockito.when(userService.getUsers(any(), any(), any())).thenReturn(list);

        mvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(list)));

    }
}
