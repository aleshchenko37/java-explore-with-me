package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.CategoryAdminService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryAdminController {

    private final CategoryAdminService adminService;

    @PostMapping
    @Validated
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto saveCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        CategoryDto categoryDto = adminService.saveCategory(newCategoryDto);
        log.info("Добавлена новая категория: {}.", categoryDto);
        return categoryDto;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Boolean deleteCategoryById(@PathVariable Long catId) {
        Boolean result = adminService.deleteCategoryById(catId);
        log.info("Удалена категория с id = {}.", catId);
        return result;
    }

    @PatchMapping("/{catId}")
    @Validated
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(
            @PathVariable Long catId, @Valid @RequestBody CategoryDto categoryDto) {
        categoryDto = adminService.updateCategory(catId, categoryDto);
        log.info("Обновлена категория с id = {}: {}.", catId, categoryDto);
        return categoryDto;
    }

}