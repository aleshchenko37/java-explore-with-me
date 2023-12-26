package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.SortSearch;
import ru.practicum.ewm.event.model.StateEvent;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.request.model.StateRequest;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.util.Statistic;
import ru.practicum.ewm.util.UtilService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static util.Constants.FORMATTER_FOR_DATETIME;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventPublicServiceImpl implements EventPublicService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final UtilService utilService;
    private final Statistic statistic;

    @Override
    public List<EventShortDto> getAllEvents(
            String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
            Boolean onlyAvailable, SortSearch sort, Integer from, Integer size, HttpServletRequest request) {
        Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "eventDate"));

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException(String.format("Дата и время не позже которых должно произойти событие " +
                            "должно быть позже даты и времени  не раньше которых должно произойти событие, " +
                            "text = %s, categories = %s, paid = %s, rangeStart = %s, rangeEnd = %s, " +
                            "onlyAvailable = %s, sort = %s, from = %s, size = %s.",
                    text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size));
        }

        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp("ewm-service");
        endpointHit.setUri(request.getRequestURI());
        endpointHit.setIp(request.getRemoteAddr());
        endpointHit.setTimestamp(LocalDateTime.now());
        statistic.statsClient.saveHit(endpointHit);

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now().minusYears(100);
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(100);
        }

        List<Event> events = eventRepository.getAllEvents(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, page);
        if (events != null) {
            for (Event event : events) {
                endpointHit.setUri("/events/" + event.getId());
                endpointHit.setTimestamp(LocalDateTime.now());
                statistic.statsClient.saveHit(endpointHit);
            }
        }
        Map<Long, Long> views = utilService.returnMapViewStats(events, rangeStart, rangeEnd);
        List<EventShortDto> eventShortDtos = EventMapper.convertEventListToEventShortDtoList(events);

        eventShortDtos.stream()
                .peek(dto -> dto.setConfirmedRequests(
                        requestRepository.countByEventIdAndStatus(dto.getId(), StateRequest.CONFIRMED)))
                .peek(dto -> dto.setViews(views.getOrDefault(dto.getId(), 0L)))
                .collect(Collectors.toList());

        if (sort.equals(SortSearch.VIEWS)) {
            eventShortDtos.sort((e1, e2) -> (int) (e2.getViews() - e1.getViews()));
        }
        return eventShortDtos;
    }

    @Override
    public EventFullDto getPublicEventById(Long eventId, HttpServletRequest request) {
        Event event = utilService.returnEvent(eventId);
        if (!event.getState().equals(StateEvent.PUBLISHED)) {
            throw new NotFoundException("Cобытие с id = " + eventId + " должно быть опубликовано.");
        }

        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp("ewm-service");
        endpointHit.setUri(request.getRequestURI());
        endpointHit.setIp(request.getRemoteAddr());
        endpointHit.setTimestamp(LocalDateTime.now());
        statistic.statsClient.saveHit(endpointHit);

        List<String> uris = List.of("/events/" + event.getId());
        List<ViewStats> viewStats = statistic.statsClient.getAllStats(
                LocalDateTime.now().minusYears(100).format(FORMATTER_FOR_DATETIME),
                LocalDateTime.now().plusYears(100).format(FORMATTER_FOR_DATETIME), uris, true);

        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        eventFullDto.setConfirmedRequests(requestRepository
                .countByEventIdAndStatus(event.getId(), StateRequest.CONFIRMED));
        eventFullDto.setViews(viewStats.isEmpty() ? 0L : viewStats.get(0).getHits());

        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getAllEventsByLocation(
            Long locId, LocalDateTime rangeStart, LocalDateTime rangeEnd,
            Integer from, Integer size, HttpServletRequest request) {

        Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "id"));
        Location location = utilService.returnLocationById(locId);

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException(String.format("Дата и время не позже которых должно произойти событие " +
                            "должно быть позже даты и времени  не раньше которых должно произойти событие, " +
                            "locId = %s, rangeStart = %s, rangeEnd = %s, from = %s, size = %s.",
                    locId, rangeStart, rangeEnd, from, size));
        }

        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp("ewm-service");
        endpointHit.setUri(request.getRequestURI());
        endpointHit.setIp(request.getRemoteAddr());
        endpointHit.setTimestamp(LocalDateTime.now());
        statistic.statsClient.saveHit(endpointHit);

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now().minusYears(100);
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(100);
        }

        List<Event> events = eventRepository.getAllEventsByLocation(location.getLat(), location.getLon(),
                location.getRadius(), rangeStart, rangeEnd, page);
        if (events != null) {
            for (Event event : events) {
                endpointHit.setUri("/events/" + event.getId());
                endpointHit.setTimestamp(LocalDateTime.now());
                statistic.statsClient.saveHit(endpointHit);
            }
        }
        Map<Long, Long> views = utilService.returnMapViewStats(events, rangeStart, rangeEnd);
        List<EventShortDto> eventShortDtos = EventMapper.convertEventListToEventShortDtoList(events);

        eventShortDtos.stream()
                .peek(dto -> dto.setConfirmedRequests(
                        requestRepository.countByEventIdAndStatus(dto.getId(), StateRequest.CONFIRMED)))
                .peek(dto -> dto.setViews(views.getOrDefault(dto.getId(), 0L)))
                .collect(Collectors.toList());

        return eventShortDtos;
    }

}