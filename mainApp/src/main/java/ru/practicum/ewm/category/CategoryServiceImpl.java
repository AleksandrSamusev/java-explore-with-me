package ru.practicum.ewm.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.CategoryNotFoundException;
import ru.practicum.ewm.exception.InvalidParameterException;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, EventRepository eventRepository) {
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
    }

    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("categoryId"));

        return CategoryMapper.toCategoryDtoList(categoryRepository.findAllCategories(pageable));

    }

    public CategoryDto getCategoryById(Long categoryId) {
        return CategoryMapper.toCategoryDto(categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found")));
    }

    public CategoryDto createCategory(CategoryDto categoryDto) {
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(categoryDto)));
    }

    public void deleteCategory(Long categoryId) {
        if (eventRepository.findAllEventsByCategory(categoryId).size() == 0) {
            categoryRepository.deleteById(categoryId);
        } else {
            throw new InvalidParameterException("Illegal operation");
        }
    }

    public CategoryDto patchCategory(CategoryDto categoryDto) {
        Category category = categoryRepository.getReferenceById(categoryDto.getId());
        if (categoryDto.getName() != null && !categoryDto.getName().equals("")) {
            category.setName(categoryDto.getName());
        }
        log.info("Updated category with ID = {} ", categoryDto.getId());
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }
}
