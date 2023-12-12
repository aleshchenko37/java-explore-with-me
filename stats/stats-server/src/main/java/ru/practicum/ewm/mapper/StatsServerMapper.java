package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.model.Hit;

public class StatsServerMapper {

    public static Hit toHit(EndpointHit endpointHit) {
        return Hit.builder()
                .app(endpointHit.getApp())
                .ip(endpointHit.getIp())
                .uri(endpointHit.getUri())
                .timestamp(endpointHit.getTimestamp())
                .build();

    }

    public static EndpointHit toEndpointHit(Hit hit) {
        return EndpointHit.builder()
                .id(hit.getId())
                .app(hit.getApp())
                .ip(hit.getIp())
                .uri(hit.getUri())
                .timestamp(hit.getTimestamp())
                .build();
    }
}