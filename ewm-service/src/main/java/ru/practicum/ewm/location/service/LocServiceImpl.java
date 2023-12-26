package ru.practicum.ewm.location.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotSaveException;
import ru.practicum.ewm.location.dto.LocationDto;
import ru.practicum.ewm.location.mapper.LocationMapper;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.location.repository.LocationRepository;
import ru.practicum.ewm.util.UtilService;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LocServiceImpl implements LocService {

    private final LocationRepository locationRepository;
    private final UtilService utilService;

    @Override
    public LocationDto saveLocation(LocationDto locationDto) {
        try {
            Location location = locationRepository.save(
                    LocationMapper.toLocation(locationDto));
            return LocationMapper.toLocationDto(location);
        } catch (DataIntegrityViolationException e) {
            throw new NotSaveException("Локация не была создана: " + locationDto);
        }
    }

    @Override
    public Boolean deleteLocationById(Long locId) {
        utilService.returnLocationById(locId);

        try {
            return locationRepository.deleteByIdWithReturnedLines(locId) > 0;
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Локация с ид = " + locId + " не может быть удалена, " +
                    "существуют события, связанные с локацией.");
        }
    }

    @Override
    public LocationDto updateLocation(Long locId, LocationDto locationDto) {
        Location location = utilService.returnLocationById(locId);

        if (locationDto.getLat() != null) {
            location.setLat(locationDto.getLat());
        }
        if (locationDto.getLon() != null) {
            location.setLon(locationDto.getLon());
        }
        if (locationDto.getRadius() != null) {
            location.setRadius(locationDto.getRadius());
        }

        try {
            return LocationMapper.toLocationDto(locationRepository.saveAndFlush(location));
        } catch (DataIntegrityViolationException e) {
            throw new NotSaveException("Локация с id = " + locId + " не была обновлена: " + locationDto);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<LocationDto> getAllLocations(Integer from, Integer size) {
        Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "id"));
        return LocationMapper.convertLocationListToLocationDTOList(
                locationRepository.findByRadiusIsGreaterThan(0f, page));
    }

    @Transactional(readOnly = true)
    @Override
    public LocationDto getLocationById(Long locId) {
        return LocationMapper.toLocationDto(utilService.returnLocationById(locId));
    }

}