package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.StateActionAdmin;
import ru.practicum.ewm.event.model.StateEvent;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotSaveException;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.request.model.StateRequest;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.util.UtilService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EventAdminServiceImpl implements EventAdminService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final UtilService utilService;

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
        Map<Long, Long> views = utilService.returnMapViewStats(events, rangeStart, rangeEnd);
        List<EventFullDto> eventFullDtos = EventMapper.convertEventListToEventFullDtoList(events);

        eventFullDtos = eventFullDtos.stream()
                .peek(dto -> dto.setConfirmedRequests(
                        requestRepository.countByEventIdAndStatus(dto.getId(), StateRequest.CONFIRMED)))
                .peek(dto -> dto.setViews(views.getOrDefault(dto.getId(), 0L)))
                .collect(Collectors.toList());

        return eventFullDtos;
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = utilService.returnEvent(eventId);
        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            Category category = utilService.returnCategory(updateEventAdminRequest.getCategory());
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
            Location location = utilService.returnLocation(updateEventAdminRequest.getLocation());
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

}