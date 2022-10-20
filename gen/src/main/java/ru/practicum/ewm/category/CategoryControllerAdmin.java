package ru.practicum.ewm.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryControllerAdmin {
    private final CategoryServiceImpl categoryService;

    @Autowired
    public CategoryControllerAdmin(CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
    }
}
