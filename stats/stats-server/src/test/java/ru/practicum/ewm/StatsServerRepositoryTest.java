package ru.practicum.ewm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.practicum.ewm.repository.StatsServerRepository;

@Testcontainers
@DataJpaTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StatsServerRepositoryTest {

    @Autowired
    private StatsServerRepository statsServerRepository;

    @Test
    void getAllStatsWithUris() {
    }

    @Test
    void getAllUniqueStatsWithUris() {
    }

    @Test
    void getAllStats() {
    }

    @Test
    void getAllUniqueStats() {
    }

}