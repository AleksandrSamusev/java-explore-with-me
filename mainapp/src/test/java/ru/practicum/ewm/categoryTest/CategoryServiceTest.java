package ru.practicum.ewm.categoryTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryDto;
import ru.practicum.ewm.category.CategoryService;
import ru.practicum.ewm.category.NewCategoryDto;
import ru.practicum.ewm.exception.CategoryConflictException;
import ru.practicum.ewm.exception.CategoryNotFoundException;
import ru.practicum.ewm.exception.InvalidParameterException;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=ewm",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)

public class CategoryServiceTest<T extends CategoryService> {

    private final EntityManager em;

    private final CategoryService categoryService;

    @Test
    public void givenNameIsOk_WhenCreateCategory_ThenCategoryCreated() {
        NewCategoryDto newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName("new_category");
        categoryService.createCategory(newCategoryDto);
        List<Category> categoryFromDb = em.createQuery("select c from Category c", Category.class).getResultList();
        assertThat(categoryFromDb.size(), equalTo(1));
        assertThat(categoryFromDb.get(0).getName(), equalTo(newCategoryDto.getName()));
    }

    @Test
    public void givenNameIsNull_WhenCreateCategory_ThenException() {
        NewCategoryDto newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName(null);
        assertThrows(InvalidParameterException.class,
                () -> categoryService.createCategory(newCategoryDto));
    }

    @Test
    public void givenNameIsBlank_WhenCreateCategory_ThenException() {
        NewCategoryDto newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName(" ");
        assertThrows(InvalidParameterException.class,
                () -> categoryService.createCategory(newCategoryDto));
    }

    @Test
    public void givenNameIsEqualToExistingCategoryName_WhenCreateCategory_ThenException() {
        NewCategoryDto newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName("new_category");
        categoryService.createCategory(newCategoryDto);
        List<Category> categoryFromDb = em.createQuery("select c from Category c", Category.class).getResultList();
        assertThat(categoryFromDb.size(), equalTo(1));
        assertThat(categoryFromDb.get(0).getName(), equalTo(newCategoryDto.getName()));

        NewCategoryDto newCategoryDto1 = new NewCategoryDto("new_category");
        assertThrows(CategoryConflictException.class,
                () -> categoryService.createCategory(newCategoryDto1));

    }

    @Test
    public void givenIdExists_WhenGetCategory_ThenReturnCategory() {
        NewCategoryDto newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName("new_category");
        categoryService.createCategory(newCategoryDto);
        assertThat(categoryService.getCategoryById(1L).getName(), equalTo("new_category"));
    }

    @Test
    public void givenIdNotExists_WhenGetCategory_ThenException() {
        NewCategoryDto newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName("new_category");
        categoryService.createCategory(newCategoryDto);
        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.getCategoryById(999L));
    }

    @Test
    public void givenNameIsOk_WhenGetCategories_ThenReturnListCategories() {
        NewCategoryDto newCategoryDto = new NewCategoryDto("category_0");
        NewCategoryDto newCategoryDto1 = new NewCategoryDto("category_1");
        NewCategoryDto newCategoryDto2 = new NewCategoryDto("category_2");
        categoryService.createCategory(newCategoryDto);
        categoryService.createCategory(newCategoryDto1);
        categoryService.createCategory(newCategoryDto2);
        assertThat(categoryService.getCategories(0, 10).size(), equalTo(3));
        assertThat(categoryService.getCategories(0, 10).get(0).getName(),
                equalTo("category_0"));
        assertThat(categoryService.getCategories(0, 10).get(1).getName(),
                equalTo("category_1"));
        assertThat(categoryService.getCategories(0, 10).get(2).getName(),
                equalTo("category_2"));
    }

    @Test
    public void givenIdExists_WhenDeleteCategory_ThenCategoryDeleted() {
        NewCategoryDto newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName("new_category");
        categoryService.createCategory(newCategoryDto);
        assertThat(categoryService.getCategories(0, 10).get(0).getId(),
                equalTo(1L));
        categoryService.deleteCategory(1L);
        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.getCategoryById(1L));
    }

    @Test
    public void givenValidCategoryDto_WhenPatchCategory_ThenCategoryPatched() {
        NewCategoryDto newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName("new_category");
        categoryService.createCategory(newCategoryDto);
        assertThat(categoryService.getCategoryById(1L).getName(), equalTo("new_category"));

        CategoryDto categoryDto = new CategoryDto(1L, "new_patched_category");
        categoryService.patchCategory(categoryDto);
        assertThat(categoryService.getCategoryById(1L).getName(), equalTo("new_patched_category"));
    }

    @Test
    public void givenCategoryDtoWithIdNotExist_WhenPatchCategory_ThenException() {
        NewCategoryDto newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName("new_category");
        categoryService.createCategory(newCategoryDto);
        assertThat(categoryService.getCategoryById(1L).getName(), equalTo("new_category"));

        CategoryDto categoryDto = new CategoryDto(999L, "new_patched_category");
        assertThrows(CategoryNotFoundException.class, () -> categoryService.patchCategory(categoryDto));
    }

    @Test
    public void givenCategoryDtoWithIdIsNull_WhenPatchCategory_ThenException() {
        NewCategoryDto newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName("new_category");
        categoryService.createCategory(newCategoryDto);
        assertThat(categoryService.getCategoryById(1L).getName(), equalTo("new_category"));

        CategoryDto categoryDto = new CategoryDto(null, "new_patched_category");
        assertThrows(InvalidParameterException.class, () -> categoryService.patchCategory(categoryDto));
    }

    @Test
    public void givenCategoryDtoWithNameIsNull_WhenPatchCategory_ThenException() {
        NewCategoryDto newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName("new_category");
        categoryService.createCategory(newCategoryDto);
        assertThat(categoryService.getCategoryById(1L).getName(), equalTo("new_category"));

        CategoryDto categoryDto = new CategoryDto(1L, null);
        assertThrows(InvalidParameterException.class, () -> categoryService.patchCategory(categoryDto));
    }

    @Test
    public void givenCategoryDtoWithNameIsBlank_WhenPatchCategory_ThenException() {
        NewCategoryDto newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName("new_category");
        categoryService.createCategory(newCategoryDto);
        assertThat(categoryService.getCategoryById(1L).getName(), equalTo("new_category"));

        CategoryDto categoryDto = new CategoryDto(1L, " ");
        assertThrows(InvalidParameterException.class, () -> categoryService.patchCategory(categoryDto));
    }

    @Test
    public void givenCategoryDtoWithNameIsExists_WhenPatchCategory_ThenException() {
        NewCategoryDto newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName("new_category");
        categoryService.createCategory(newCategoryDto);
        assertThat(categoryService.getCategoryById(1L).getName(), equalTo("new_category"));

        CategoryDto categoryDto = new CategoryDto(1L, "new_category");
        assertThrows(CategoryConflictException.class, () -> categoryService.patchCategory(categoryDto));
    }

}
