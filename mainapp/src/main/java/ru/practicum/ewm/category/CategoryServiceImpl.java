package ru.practicum.ewm.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.CategoryConflictException;
import ru.practicum.ewm.exception.CategoryNotFoundException;
import ru.practicum.ewm.exception.InvalidParameterException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, EventRepository eventRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        return CategoryMapper.toCategoryDtoList(categoryRepository.findAllCategories(pageable));

    }

    public CategoryDto getCategoryById(Long categoryId) {
        return CategoryMapper.toCategoryDto(categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found")));
    }

    public CategoryDto createCategory(CategoryDto categoryDto) {
        validateCategoryDtoCreate(categoryDto);
        log.info("New category was created");
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(categoryDto)));
    }

    public void deleteCategory(Long categoryId) {
        log.info("Category with id = {} was deleted", categoryId);
        categoryRepository.deleteById(categoryId);
    }

    public CategoryDto patchCategory(CategoryDto categoryDto) {
        validateCategoryDto(categoryDto);
        Category category = categoryRepository.findById(categoryDto.getId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        category.setName(categoryDto.getName());
        log.info("Updated category with ID = {} ", categoryDto.getId());
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    private void validateCategoryDto(CategoryDto categoryDto) {
        if (categoryDto.getId() == null) {
            log.info("Mandatory field ID is absent");
            throw new InvalidParameterException("Category id is absent");
        }
        if (categoryDto.getName() == null || categoryDto.getName().isBlank()) {
            log.info("Mandatory field NAME is invalid");
            throw new InvalidParameterException("Not valid NAME parameter");
        }
        if (isCategoryExistsByName(categoryDto.getName())) {
            log.info("Category with the name - {} already exists", categoryDto.getName());
            throw new CategoryConflictException("Category with this name already exists");
        }
    }

    private void validateCategoryDtoCreate(CategoryDto categoryDto) {
        if (categoryDto.getName() == null || categoryDto.getName().isBlank()) {
            log.info("Mandatory field NAME not valid");
            throw new InvalidParameterException("Not valid parameter");
        }
        if (isCategoryExistsByName(categoryDto.getName())) {
            log.info("Category with the name - {} already exists", categoryDto.getName());
            throw new CategoryConflictException("Category with this name already exists");
        }
    }

    private boolean isCategoryExistsByName(String name) {
        Optional<Category> category = Optional.ofNullable(categoryRepository.findByNameContainingIgnoreCase(name));
        return category.isPresent();
    }
}
