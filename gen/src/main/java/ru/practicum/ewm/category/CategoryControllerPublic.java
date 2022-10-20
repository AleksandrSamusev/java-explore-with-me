package ru.practicum.ewm.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryControllerPublic {
    private final CategoryServiceImpl categoryService;

    @Autowired
    public CategoryControllerPublic(CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
    }
}
