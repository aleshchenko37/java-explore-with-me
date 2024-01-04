package ru.practicum.ewm.location;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.BaseDataJpaTest;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.location.repository.LocationRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class LocationRepositoryTest extends BaseDataJpaTest {

    @Autowired
    private LocationRepository locationRepository;

    private final Location location1 = new Location();
    private final Location location2 = new Location();

    @BeforeEach
    public void addLocations() {
        location1.setLat(23.98f);
        location1.setLon(94.34f);
        location1.setRadius(3.12f);
        locationRepository.save(location1);

        location2.setLat(53.98f);
        location2.setLon(90.34f);
        location2.setRadius(10.12f);
        locationRepository.save(location2);
    }

    @AfterEach
    public void deleteLocations() {
        locationRepository.deleteAll();
    }


    @Test
    void findByLatAndLonAndRadius() {
    }

    @Test
    void deleteByIdWithReturnedLines() {
    }

    @Test
    @DisplayName("получены все локации с радиусом больше указанного, когда вызвана, " +
            "то получен список локаций")
    void findByRadiusIsGreaterThan() {
        List<Location> actualLocations = locationRepository
                .findByRadiusIsGreaterThan(10f, PageRequest.of(0, 10));

        assertThat(1, equalTo(actualLocations.size()));
        assertThat(location2, equalTo(actualLocations.get(0)));
    }

}