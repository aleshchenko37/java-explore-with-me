package ru.practicum.ewm.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.ewm.BaseDataJpaTest;
import ru.practicum.ewm.request.repository.RequestRepository;

class RequestRepositoryTest extends BaseDataJpaTest {

    @Autowired
    private RequestRepository requestRepository;

    @Test
    void findAllByRequesterId() {
    }

    @Test
    void findAllByEventId() {
    }

    @Test
    void findAllByIdIn() {
    }

    @Test
    void countByRequesterIdAndEventId() {
    }

    @Test
    void countByEventIdAndStatus() {
    }

}