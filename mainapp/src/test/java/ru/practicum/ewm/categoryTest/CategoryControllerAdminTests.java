package ru.practicum.ewm.categoryTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.ewm.category.CategoryControllerAdmin;
import ru.practicum.ewm.category.CategoryDto;
import ru.practicum.ewm.category.CategoryServiceImpl;

import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerAdminTests {

    @Mock
    private CategoryServiceImpl categoryService;

    @InjectMocks
    private CategoryControllerAdmin controller;


    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();
    private final CategoryDto categoryDto = new CategoryDto(1L, "category");

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(InvalidParameterException.class)
                .build();
    }

    @Test
    public void shouldReturnStatus200WhenCreateCategory() throws Exception {
        when(categoryService.createCategory(any()))
                .thenReturn(categoryDto);

        mvc.perform(post("/admin/categories")
                        .content(mapper.writeValueAsString(categoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("category"));
    }

    @Test
    public void shouldReturnStatus200WhenDeleteCategory() throws Exception {

        Long categoryId = 1L;
        mvc.perform(delete("/admin/categories/1")
                        .content(mapper.writeValueAsString(categoryId))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
    }

    @Test
    public void shouldReturnStatus200WhenPatchCategory() throws Exception {

        CategoryDto dto = new CategoryDto(1L, "category");
        CategoryDto patchedDto = new CategoryDto(1L, "patched category");

        when(categoryService.patchCategory(dto))
                .thenReturn(patchedDto);

        mvc.perform(patch("/admin/categories")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("patched category"));
    }
}
