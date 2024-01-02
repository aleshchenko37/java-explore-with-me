package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotSaveException;
import ru.practicum.ewm.util.UtilService;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryAdminServiceImpl implements CategoryAdminService {

    private final CategoryRepository categoryRepository;
    private final UtilService utilService;

    @Override
    public CategoryDto saveCategory(NewCategoryDto newCategoryDto) {
        try {
            Category category = categoryRepository.save(
                    CategoryMapper.toCategoryFromNewDto(newCategoryDto));
            return CategoryMapper.toCategoryDto(category);
        } catch (DataIntegrityViolationException e) {
            throw new NotSaveException("Категория не была создана: " + newCategoryDto);
        }
    }

    @Override
    public Boolean deleteCategoryById(Long catId) {
        utilService.returnCategory(catId);

        try {
            return categoryRepository.deleteByIdWithReturnedLines(catId) > 0;
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Категория с ид = " + catId + " не может быть удалена, " +
                    "существуют события, связанные с категорией.");
        }
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category category = utilService.returnCategory(catId);
        category.setName(categoryDto.getName());

        try {
            return CategoryMapper.toCategoryDto(categoryRepository.saveAndFlush(category));
        } catch (DataIntegrityViolationException e) {
            throw new NotSaveException("Категория с id = " + catId + " не была обновлена: " + categoryDto);
        }
    }

}