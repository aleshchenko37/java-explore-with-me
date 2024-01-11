package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.StateActionAdmin;
import ru.practicum.ewm.event.model.StateEvent;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.NotSaveException;
import ru.practicum.ewm.location.dto.LocationDto;
import ru.practicum.ewm.location.mapper.LocationMapper;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.location.repository.LocationRepository;
import ru.practicum.ewm.request.model.StateRequest;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.statistic.Statistic;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static util.Constants.FORMATTER_FOR_DATETIME;

@Service
@Transactional
@RequiredArgsConstructor
public class EventAdminServiceImpl implements EventAdminService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final Statistic statistic;

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = returnEvent(eventId);
        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            Category category = returnCategory(updateEventAdminRequest.getCategory());
            event.setCategory(category);
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null &&
                LocalDateTime.now().plusHours(1).isAfter(updateEventAdminRequest.getEventDate())) {
            throw new BadRequestException(String.format("Дата начала изменяемого события должна быть не ранее, " +
                            "чем за час от даты публикации, eventId = %s, updateEventAdminRequest: %s.",
                    eventId, updateEventAdminRequest));
        }
        if (updateEventAdminRequest.getLocation() != null) {
            Location location = returnLocation(updateEventAdminRequest.getLocation());
            event.setLocation(location);
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction().equals(StateActionAdmin.PUBLISH_EVENT) &&
                    !event.getState().equals(StateEvent.PENDING)) {
                throw new ConflictException(String.format("Событие можно публиковать только, если оно в состоянии " +
                                "ожидания публикации, eventId = %s, updateEventAdminRequest: %s.",
                        eventId, updateEventAdminRequest));
            }
            if (updateEventAdminRequest.getStateAction().equals(StateActionAdmin.REJECT_EVENT) &&
                    event.getState().equals(StateEvent.PUBLISHED)) {
                throw new ConflictException(String.format("событие можно отклонить только, если оно еще " +
                                "не опубликовано, eventId = %s, updateEventAdminRequest: %s.",
                        eventId, updateEventAdminRequest));
            }

            switch (updateEventAdminRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(StateEvent.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(StateEvent.CANCELED);
                    break;
            }
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        try {
            return EventMapper.toEventFullDto(eventRepository.saveAndFlush(event));
        } catch (DataIntegrityViolationException e) {
            throw new NotSaveException("Событие с id = " + eventId + ", не было обновлено: " +
                    updateEventAdminRequest);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> getAllEventsByAdmin(
            List<Long> users, List<StateEvent> states, List<Long> categories,
            LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "id"));

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }

        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(100);
        }

        List<Event> events = eventRepository.getAllEventsByAdmin(users, states,
                categories, rangeStart, rangeEnd, page);
        Map<Long, Long> views = returnMapViewStats(events, rangeStart, rangeEnd);
        List<EventFullDto> eventFullDtos = EventMapper.convertEventListToEventFullDtoList(events);

        eventFullDtos = eventFullDtos.stream()
                .peek(dto -> dto.setConfirmedRequests(
                        requestRepository.countByEventIdAndStatus(dto.getId(), StateRequest.CONFIRMED)))
                .peek(dto -> dto.setViews(views.getOrDefault(dto.getId(), 0L)))
                .collect(Collectors.toList());

        return eventFullDtos;
    }

    private Event returnEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с идентификатором " + eventId + " не найдено."));
    }

    private Category returnCategory(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id = " + catId + " не найден."));
    }

    @Transactional
    private Location returnLocation(LocationDto locationDto) {
        locationDto.setRadius(0f);
        Location location = locationRepository
                .findByLatAndLonAndRadius(locationDto.getLat(), locationDto.getLon(), 0f);
        return location != null ? location
                : locationRepository.save(LocationMapper.toLocation(locationDto));
    }

    private Map<Long, Long> returnMapViewStats(List<Event> events, LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        List<String> uris = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());
        List<ViewStats> viewStatList = statistic.statsClient.getAllStats(rangeStart.format(FORMATTER_FOR_DATETIME),
                rangeEnd.format(FORMATTER_FOR_DATETIME), uris, true);

        Map<Long, Long> views = new HashMap<>();
        for (ViewStats viewStats : viewStatList) {
            Long id = Long.parseLong(viewStats.getUri().split("/events/")[1]);
            views.put(id, views.getOrDefault(id, 0L) + 1);
        }
        return views;
    }
}