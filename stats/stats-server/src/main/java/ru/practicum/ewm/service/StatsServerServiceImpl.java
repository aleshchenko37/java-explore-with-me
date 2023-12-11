package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.HitNotSaveException;
import ru.practicum.ewm.mapper.StatsServerMapper;
import ru.practicum.ewm.model.Hit;
import ru.practicum.ewm.repository.StatsServerRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServerServiceImpl implements StatsServerService {

    private final StatsServerRepository statsServerRepository;

    @Transactional
    @Override
    public EndpointHit saveEndpointHit(EndpointHit endpointHit) {
        try {
            Hit hit = statsServerRepository.save(StatsServerMapper.INSTANCE.toHit(endpointHit));
            return StatsServerMapper.INSTANCE.toEndpointHit(hit);
        } catch (DataIntegrityViolationException e) {
            throw new HitNotSaveException("Информация о запросе не была сохранена: " + endpointHit);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<ViewStats> getAllStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (end.isBefore(start)) {
            throw new BadRequestException("Дата и время конца должны быть после даты и времени начала.");
        }

        if (unique) {
            if (uris == null) {
                return statsServerRepository.getAllUniqueStats(start, end);
            } else {
                return statsServerRepository.getAllUniqueStatsWithUris(start, end, uris);
            }
        } else {
            if (uris == null) {
                return statsServerRepository.getAllStats(start, end);
            } else {
                return statsServerRepository.getAllStatsWithUris(start, end, uris);
            }
        }
    }

}
