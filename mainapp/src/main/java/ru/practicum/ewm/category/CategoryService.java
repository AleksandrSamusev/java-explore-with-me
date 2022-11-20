package ru.practicum.ewm.category;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long categoryId);

    CategoryDto createCategory(NewCategoryDto categoryDto);

    void deleteCategory(Long categoryId);

    CategoryDto patchCategory(CategoryDto categoryDto);
}
