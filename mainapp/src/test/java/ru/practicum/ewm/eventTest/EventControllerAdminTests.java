package ru.practicum.ewm.eventTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.event.EventControllerAdmin;
import ru.practicum.ewm.event.EventFullDto;
import ru.practicum.ewm.event.EventServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EventControllerAdmin.class)
public class EventControllerAdminTests {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private EventServiceImpl eventService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void shouldReturnStatus200WhenFindAllUsersEventsFull() throws Exception {
        List<EventFullDto> list = new ArrayList<>();

        when(eventService.findAllUsersEventsFull(any(), any(), any(), any(), any(), any(), any())).thenReturn(list);

        mvc.perform(get("/admin/events?users=1&states=PUBLISHED&categories=1" +
                        "&rangeStart=null&rangeEnd=null&from=0&size=10"))
                .andExpect(status().isOk());
    }


}
