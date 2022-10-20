package ru.practicum.ewm.category;

import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public static CategoryDto toCategoryDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryId(category.getCategoryId());
        categoryDto.setName(category.getName());
        return categoryDto;
    }

    public static Category toCategory(CategoryDto categoryDto) {
        Category category = new Category();
        category.setCategoryId(categoryDto.getCategoryId());
        category.setName(categoryDto.getName());
        return category;
    }
}
