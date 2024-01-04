package ru.practicum.ewm.location.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.location.dto.LocationDto;
import ru.practicum.ewm.location.service.LocationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/locations")
@RequiredArgsConstructor
@Slf4j
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    @Validated
    @ResponseStatus(HttpStatus.CREATED)
    public LocationDto saveLocation(@Valid @RequestBody LocationDto locationDto) {
        locationDto = locationService.saveLocation(locationDto);
        log.info("Добавлена новая локация: {}.", locationDto);
        return locationDto;
    }

    @DeleteMapping("/{locId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Boolean deleteLocationById(@PathVariable Long locId) {
        Boolean result = locationService.deleteLocationById(locId);
        log.info("Удалена локация с id = {}.", locId);
        return result;
    }

    @PatchMapping("/{locId}")
    public LocationDto updateLocation(
            @PathVariable Long locId, @RequestBody LocationDto locationDto) {
        locationDto = locationService.updateLocation(locId, locationDto);
        log.info("Обновлена локация с id = {}: {}.", locId, locationDto);
        return locationDto;
    }

    @GetMapping
    @Validated
    @ResponseStatus(HttpStatus.OK)
    public List<LocationDto> getAllLocations(
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        List<LocationDto> locationDtos = locationService.getAllLocations(from, size);
        log.info("Получен список конкретных локаций, from = {}, size = {}.", from, size);
        return locationDtos;
    }

    @GetMapping("/{locId}")
    @ResponseStatus(HttpStatus.OK)
    public LocationDto getLocationById(@PathVariable Long locId) {
        LocationDto locationDto = locationService.getLocationById(locId);
        log.info("Получена локация с id = {}.", locId);
        return locationDto;
    }

}