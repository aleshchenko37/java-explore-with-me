package ru.practicum.ewm.location.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.location.dto.LocationDto;
import ru.practicum.ewm.location.service.LocService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/locations")
@RequiredArgsConstructor
@Slf4j
public class LocController {

    private final LocService locService;

    @PostMapping
    @Validated
    public ResponseEntity<LocationDto> saveLocation(@Valid @RequestBody LocationDto locationDto) {
        locationDto = locService.saveLocation(locationDto);
        log.info("Добавлена новая локация: {}.", locationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(locationDto);
    }

    @DeleteMapping("/{locId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Boolean> deleteLocationById(@PathVariable Long locId) {
        Boolean result = locService.deleteLocationById(locId);
        log.info("Удалена локация с id = {}.", locId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
    }

    @PatchMapping("/{locId}")
    public ResponseEntity<LocationDto> updateLocation(
            @PathVariable Long locId, @RequestBody LocationDto locationDto) {
        locationDto = locService.updateLocation(locId, locationDto);
        log.info("Обновлена локация с id = {}: {}.", locId, locationDto);
        return ResponseEntity.ok(locationDto);
    }

    @GetMapping
    public ResponseEntity<List<LocationDto>> getAllLocations(
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        List<LocationDto> locationDtos = locService.getAllLocations(from, size);
        log.info("Получен список конкретных локаций, from = {}, size = {}.", from, size);
        return ResponseEntity.ok().body(locationDtos);
    }

    @GetMapping("/{locId}")
    public ResponseEntity<LocationDto> getLocationById(@PathVariable Long locId) {
        LocationDto locationDto = locService.getLocationById(locId);
        log.info("Получена локация с id = {}.", locId);
        return ResponseEntity.ok(locationDto);
    }

}