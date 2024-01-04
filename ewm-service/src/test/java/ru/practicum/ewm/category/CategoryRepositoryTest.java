package ru.practicum.ewm.category;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.ewm.BaseDataJpaTest;
import ru.practicum.ewm.category.repository.CategoryRepository;

class CategoryRepositoryTest extends BaseDataJpaTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void deleteByIdWithReturnedLines() {
    }

}