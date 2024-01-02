package ru.practicum.ewm.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.category.service.CategoryAdminServiceImpl;
import ru.practicum.ewm.exception.NotSaveException;
import ru.practicum.ewm.util.UtilService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryAdminServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UtilService utilService;

    @InjectMocks
    private CategoryAdminServiceImpl adminService;

    @Test
    @DisplayName("сохранена категория, когда категория валидна, тогда она сохраняется")
    void saveCategory_whenCategoryValid_thenSavedCategory() {
        NewCategoryDto categoryToSave = new NewCategoryDto();
        categoryToSave.setName("name");
        when(categoryRepository.save(any(Category.class)))
                .thenReturn(CategoryMapper.toCategoryFromNewDto(categoryToSave));

        CategoryDto actualCategory = adminService.saveCategory(categoryToSave);

        assertThat(categoryToSave.getName(), equalTo(actualCategory.getName()));
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("сохранена категория, когда категория не валидна, тогда выбрасывается исключение")
    void saveCategory_whenCategoryNotValid_thenExceptionThrown() {
        NewCategoryDto categoryToSave = new NewCategoryDto();
        when(categoryRepository.save(any(Category.class)))
                .thenThrow(new DataIntegrityViolationException("Категория не была создана."));

        final NotSaveException exception = assertThrows(NotSaveException.class,
                () -> adminService.saveCategory(categoryToSave));

        assertThat("Категория не была создана: " + categoryToSave,
                equalTo(exception.getMessage()));
        verify(categoryRepository, times(1)).save(any(Category.class));
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("удалена категория, когда вызвано, тогда она удаляется")
    void deleteCategory_whenInvoked_thenDeletedCategory() {
        Long catId = 0L;
        when(utilService.returnCategory(anyLong())).thenReturn(new Category());
        when(categoryRepository.deleteByIdWithReturnedLines(anyLong())).thenReturn(1);

        Boolean actualResult = adminService.deleteCategoryById(catId);

        assertThat(true, equalTo(actualResult));
        InOrder inOrder = inOrder(utilService, categoryRepository);
        inOrder.verify(utilService, times(1)).returnCategory(catId);
        inOrder.verify(categoryRepository, times(1)).deleteByIdWithReturnedLines(anyLong());
    }

    @Test
    @DisplayName("обновлена категория, когда категория валидна, тогда она обновляется")
    void updateCategory_whenCategoryFound_thenUpdatedCategory() {
        Long catId = 0L;
        Category oldCategory = new Category();
        oldCategory.setName("1");
        when(utilService.returnCategory(anyLong())).thenReturn(oldCategory);

        Category newCategory = new Category();
        newCategory.setName("2");
        when(categoryRepository.saveAndFlush(any(Category.class))).thenReturn(newCategory);

        CategoryDto actualCategory = adminService
                .updateCategory(catId, CategoryMapper.toCategoryDto(newCategory));

        assertThat(newCategory.getName(), equalTo(actualCategory.getName()));
        InOrder inOrder = inOrder(utilService, categoryRepository);
        inOrder.verify(utilService, times(1)).returnCategory(anyLong());
        inOrder.verify(categoryRepository, times(1)).saveAndFlush(any(Category.class));
    }

}