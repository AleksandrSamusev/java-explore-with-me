package ru.practicum.ewm.categoryTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.category.CategoryControllerPublic;
import ru.practicum.ewm.category.CategoryDto;
import ru.practicum.ewm.category.CategoryServiceImpl;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CategoryControllerPublic.class)
public class CategoryControllerPublicTests {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CategoryServiceImpl categoryService;

    @Autowired
    private MockMvc mvc;
    private final CategoryDto categoryDto = new CategoryDto(1L, "category");

    @Test
    public void getAllCategoriesTest() throws Exception {
        CategoryDto categoryDto1 = new CategoryDto();
        categoryDto1.setName("category1");

        CategoryDto categoryDto2 = new CategoryDto();
        categoryDto2.setName("category2");

        List<CategoryDto> list = new ArrayList<>();
        list.add(categoryDto1);
        list.add(categoryDto2);

        Mockito.when(categoryService.getCategories(any(), any())).thenReturn(list);

        mvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(list)));
    }

    @Test
    void getCategoryById() throws Exception {

        CategoryDto categoryDto = new CategoryDto(1L, "category");

        Mockito.when(categoryService.getCategoryById(Mockito.any())).thenReturn(categoryDto);

        mvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("category"));

    }

    @Test
    void getIncorrectCategoryById() throws Exception {

        Mockito.when(categoryService.getCategoryById(Mockito.any())).thenThrow(NotFoundException.class);

        mvc.perform(get("/categories/1"))
                .andExpect(status().isNotFound());
    }


}
