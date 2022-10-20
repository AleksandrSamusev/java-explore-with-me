package ru.practicum.ewm.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/admin/categories")
public class CategoryControllerAdmin {

    private final CategoryServiceImpl categoryService;

    @Autowired
    public CategoryControllerAdmin(CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public CategoryDto createCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.createCategory(categoryDto);
    }

    @DeleteMapping("/{categoryId}")
    public void deleteCategory(Long categoryId) {
        categoryService.deleteCategory(categoryId);
    }

    @PatchMapping
    public CategoryDto patchCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.patchCategory(categoryDto);
    }
}
