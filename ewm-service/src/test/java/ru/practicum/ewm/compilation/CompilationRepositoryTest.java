package ru.practicum.ewm.compilation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.ewm.BaseDataJpaTest;
import ru.practicum.ewm.compilation.repository.CompilationRepository;

class CompilationRepositoryTest extends BaseDataJpaTest {

    @Autowired
    private CompilationRepository compilationRepository;

    @Test
    void findAllByPinned() {
    }

}