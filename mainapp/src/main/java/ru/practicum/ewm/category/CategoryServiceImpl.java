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

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        return CategoryMapper.toCategoryDtoList(categoryRepository.findAllCategories(pageable));

    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        return CategoryMapper.toCategoryDto(categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found")));
    }

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        validateNewCategoryDto(newCategoryDto);
        Category savedCategory = categoryRepository.save(CategoryMapper.toCategoryFromNew(newCategoryDto));
        log.info("Category with id= {} created", savedCategory.getId());
        return CategoryMapper.toCategoryDto(savedCategory);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        log.info("Category with id = {} was deleted", categoryId);
        categoryRepository.deleteById(categoryId);
    }

    @Override
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
            log.info("Category name - \"{}\".Mandatory field ID is absent", categoryDto.getName());
            throw new InvalidParameterException("Category id is absent");
        }
        if (categoryDto.getName() == null || categoryDto.getName().isBlank()) {
            log.info("Category id - \"{}\".Mandatory field NAME is invalid", categoryDto.getId());
            throw new InvalidParameterException("Not valid NAME parameter");
        }
        if (isCategoryExistsByName(categoryDto.getName())) {
            log.info("Category id - \"{}\".Name - {} already exists", categoryDto.getId(), categoryDto.getName());
            throw new CategoryConflictException("Category with this name already exists");
        }
    }

    private void validateNewCategoryDto(NewCategoryDto newCategoryDto) {
        if (newCategoryDto.getName() == null || newCategoryDto.getName().isBlank()) {
            log.info("Mandatory field NAME not valid. name = \"{}\"", newCategoryDto.getName());
            throw new InvalidParameterException("Not valid parameter");
        }
        if (isCategoryExistsByName(newCategoryDto.getName())) {
            log.info("Category with the name - {} already exists", newCategoryDto.getName());
            throw new CategoryConflictException("Category with this name already exists");
        }
    }

    private boolean isCategoryExistsByName(String name) {
        Optional<Category> category = Optional.ofNullable(categoryRepository.findByNameContainingIgnoreCase(name));
        return category.isPresent();
    }
}
