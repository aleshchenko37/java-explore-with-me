package ru.practicum.ewm.location.mapper;

import ru.practicum.ewm.location.dto.LocationDto;
import ru.practicum.ewm.location.model.Location;

import java.util.List;
import java.util.stream.Collectors;

public class LocationMapper {

    public static Location toLocation(LocationDto locationDto) {
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .radius(locationDto.getRadius())
                .build();
    }

    public static LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .id(location.getId())
                .lat(location.getLat())
                .lon(location.getLon())
                .radius(location.getRadius())
                .build();
    }

    public static List<LocationDto> convertLocationListToLocationDTOList(List<Location> locations) {
        return locations.stream().map(LocationMapper::toLocationDto).collect(Collectors.toList());
    }

}