package ru.practicum.ewm.location.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.location.model.Location;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    Location findByLatAndLonAndRadius(Float lat, Float lon, Float radius);

    @Modifying
    @Query("delete from Location l where l.id = ?1")
    Integer deleteByIdWithReturnedLines(Long locId);

    List<Location> findByRadiusIsGreaterThan(Float radius, Pageable page);

}