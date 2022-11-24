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
        NewCategoryDto newCategoryDto = new NewCategoryDto("new_category");
        categoryService.createCategory(newCategoryDto);

        List<Category> categoryFromDb = em.createQuery("select c from Category c",
                Category.class).getResultList();

        assertThat(categoryFromDb.get(0).getName(), equalTo(newCategoryDto.getName()));
    }

    @Test
    public void givenNameIsNull_WhenCreateCategory_ThenException() {
        NewCategoryDto newCategoryDto = new NewCategoryDto(null);

        assertThrows(InvalidParameterException.class,
                () -> categoryService.createCategory(newCategoryDto));
    }

    @Test
    public void givenNameIsBlank_WhenCreateCategory_ThenException() {
        NewCategoryDto newCategoryDto = new NewCategoryDto("  ");

        assertThrows(InvalidParameterException.class,
                () -> categoryService.createCategory(newCategoryDto));
    }

    @Test
    public void givenNameIsEqualToExistingCategoryName_WhenCreateCategory_ThenException() {

        NewCategoryDto newCategoryDto1 = new NewCategoryDto("new_category");
        NewCategoryDto newCategoryDto2 = new NewCategoryDto("new_category");

        categoryService.createCategory(newCategoryDto1);

        assertThrows(CategoryConflictException.class,
                () -> categoryService.createCategory(newCategoryDto2));

    }

    @Test
    public void givenIdExists_WhenGetCategory_ThenReturnCategoryReturned() {
        NewCategoryDto newCategoryDto = new NewCategoryDto("new_category");
        categoryService.createCategory(newCategoryDto);

        assertThat(categoryService.getCategoryById(1L).getName(), equalTo("new_category"));
    }

    @Test
    public void givenIdNotExists_WhenGetCategory_ThenException() {

        NewCategoryDto newCategoryDto = new NewCategoryDto("new_category");
        categoryService.createCategory(newCategoryDto);

        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.getCategoryById(999L));
    }

    @Test
    public void givenCategories_WhenGetCategories_ThenReturnListCategories() {

        NewCategoryDto newCategoryDto = new NewCategoryDto("category_0");
        NewCategoryDto newCategoryDto1 = new NewCategoryDto("category_1");
        NewCategoryDto newCategoryDto2 = new NewCategoryDto("category_2");
        categoryService.createCategory(newCategoryDto);
        categoryService.createCategory(newCategoryDto1);
        categoryService.createCategory(newCategoryDto2);

        List<CategoryDto> list = categoryService.getCategories(0, 10);

        assertThat(list.size(), equalTo(3));
        assertThat(list.get(0).getName(), equalTo("category_0"));
        assertThat(list.get(1).getName(), equalTo("category_1"));
        assertThat(list.get(2).getName(), equalTo("category_2"));
    }

    @Test
    public void givenIdExists_WhenDeleteCategory_ThenCategoryDeleted() {

        NewCategoryDto newCategoryDto = new NewCategoryDto("new_category");
        categoryService.createCategory(newCategoryDto);

        categoryService.deleteCategory(1L);

        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.getCategoryById(1L));
    }

    @Test
    public void givenValidCategoryDto_WhenPatchCategory_ThenCategoryPatched() {

        NewCategoryDto newCategory = new NewCategoryDto("new_category");
        CategoryDto patchedCategory = new CategoryDto(1L, "new_patched_category");
        categoryService.createCategory(newCategory);

        categoryService.patchCategory(patchedCategory);

        assertThat(categoryService.getCategoryById(1L).getName(), equalTo("new_patched_category"));
    }

    @Test
    public void givenCategoryDtoWithIdNotExist_WhenPatchCategory_ThenException() {

        NewCategoryDto newCategory = new NewCategoryDto("new_category");
        CategoryDto patchedCategory = new CategoryDto(999L, "new_patched_category");

        categoryService.createCategory(newCategory);

        assertThrows(CategoryNotFoundException.class, () -> categoryService.patchCategory(patchedCategory));
    }

    @Test
    public void givenCategoryDtoWithIdIsNull_WhenPatchCategory_ThenException() {

        NewCategoryDto newCategory = new NewCategoryDto("new_category");
        CategoryDto patchedCategory = new CategoryDto(null, "new_patched_category");

        categoryService.createCategory(newCategory);

        assertThrows(InvalidParameterException.class, () -> categoryService.patchCategory(patchedCategory));
    }

    @Test
    public void givenCategoryDtoWithNameIsNull_WhenPatchCategory_ThenException() {

        NewCategoryDto newCategory = new NewCategoryDto("new_category");
        CategoryDto patchedCategory = new CategoryDto(1L, null);

        categoryService.createCategory(newCategory);

        assertThrows(InvalidParameterException.class, () -> categoryService.patchCategory(patchedCategory));
    }

    @Test
    public void givenCategoryDtoWithNameIsBlank_WhenPatchCategory_ThenException() {

        NewCategoryDto newCategory = new NewCategoryDto("new_category");
        CategoryDto patchedCategory = new CategoryDto(1L, "  ");

        categoryService.createCategory(newCategory);

        assertThrows(InvalidParameterException.class, () -> categoryService.patchCategory(patchedCategory));
    }

    @Test
    public void givenCategoryDtoWithNameIsExists_WhenPatchCategory_ThenException() {

        NewCategoryDto newCategory = new NewCategoryDto("new_category");
        CategoryDto patchedCategory = new CategoryDto(1L, "new_category");

        categoryService.createCategory(newCategory);

        assertThrows(CategoryConflictException.class, () -> categoryService.patchCategory(patchedCategory));
    }

}
