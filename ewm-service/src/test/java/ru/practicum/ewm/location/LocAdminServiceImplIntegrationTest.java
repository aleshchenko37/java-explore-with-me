package ru.practicum.ewm.location;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.location.dto.LocationDto;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.location.service.LocServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class LocAdminServiceImplIntegrationTest {

    private final EntityManager em;
    private final LocServiceImpl locService;

    @Test
    @DisplayName("сохранена локация, когда локация валидна, тогда она сохраняется")
    void saveLocation() {
        LocationDto locationDto = makeLocationDto(45.89f, -15.34f, 300f);
        locationDto = locService.saveLocation(locationDto);

        TypedQuery<Location> query = em.createQuery("Select l from Location l where l.id = :id", Location.class);
        Location location = query
                .setParameter("id", locationDto.getId())
                .getSingleResult();

        assertThat(location.getId(), equalTo(locationDto.getId()));
        assertThat(location.getLat(), equalTo(locationDto.getLat()));
        assertThat(location.getLon(), equalTo(locationDto.getLon()));
        assertThat(location.getRadius(), equalTo(locationDto.getRadius()));
    }

    @Test
    @DisplayName("удалена локация, когда локация найдена, тогда она удаляется")
    void deleteLocationById() {
        LocationDto locationDto = makeLocationDto(45.89f, -15.34f, 300f);
        locationDto = locService.saveLocation(locationDto);
        Long locId = locationDto.getId();
        locService.deleteLocationById(locId);

        TypedQuery<Location> query = em.createQuery("Select l from Location l where l.id = :id", Location.class);
        int result = query
                .setParameter("id", locId)
                .getFirstResult();

        assertThat(0, equalTo(result));
    }

    private LocationDto makeLocationDto(Float lat, Float lon, Float radius) {
        LocationDto locationDto = new LocationDto();
        locationDto.setLat(lat);
        locationDto.setLon(lon);
        locationDto.setRadius(radius);

        return locationDto;
    }

}