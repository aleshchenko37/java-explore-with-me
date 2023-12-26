package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.StateActionUser;
import ru.practicum.ewm.event.model.StateEvent;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotSaveException;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.request.model.ParticipationRequest;
import ru.practicum.ewm.request.model.StateRequest;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.util.Statistic;
import ru.practicum.ewm.util.UtilService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static util.Constants.FORMATTER_FOR_DATETIME;

@Service
@Transactional
@RequiredArgsConstructor
public class EventPrivateServiceImpl implements EventPrivateService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final UtilService utilService;
    private final Statistic statistic;

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getAllEventsByUser(Long userId, Integer from, Integer size) {
        utilService.returnUser(userId);
        Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "id"));
        return EventMapper.convertEventListToEventShortDtoList(
                eventRepository.findByInitiatorId(userId, page));
    }

    @Override
    public EventFullDto saveEvent(Long userId, NewEventDto newEventDto) {
        User user = utilService.returnUser(userId);
        Category category = utilService.returnCategory(newEventDto.getCategory());
        if (!newEventDto.getEventDate().isAfter(LocalDateTime.now())) {
            throw new ConflictException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. " +
                    "Value: " + newEventDto.getEventDate());
        }
        Location location = utilService.returnLocation(newEventDto.getLocation());

        Event event = EventMapper.toEventFromNewDto(newEventDto, user, category, location);
        event.setConfirmedRequests(0L);
        event.setConfirmedRequests(0L);
        event.setCreatedOn(LocalDateTime.now());
        event.setLocation(location);
        event.setPublishedOn(LocalDateTime.now());
        event.setState(StateEvent.PENDING);

        try {
            event = eventRepository.save(event);
            EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
            eventFullDto.setViews(0L);
            return eventFullDto;
        } catch (DataIntegrityViolationException e) {
            throw new NotSaveException("Событие не было создано: " + newEventDto);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getEventById(Long userId, Long eventId) {
        utilService.returnUser(userId);
        Event event = utilService.returnEvent(eventId);
        utilService.checkEventInitiator(event, userId);

        List<String> uris = List.of("/events/" + eventId);
        List<ViewStats> viewStats = statistic.statsClient.getAllStats(
                LocalDateTime.now().minusYears(100).format(FORMATTER_FOR_DATETIME),
                LocalDateTime.now().plusYears(100).format(FORMATTER_FOR_DATETIME), uris, true);

        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        eventFullDto.setConfirmedRequests(requestRepository
                .countByEventIdAndStatus(eventId, StateRequest.CONFIRMED));
        eventFullDto.setViews(viewStats.isEmpty() ? 0L : viewStats.get(0).getHits());

        return eventFullDto;
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        utilService.returnUser(userId);
        Event event = utilService.returnEvent(eventId);
        utilService.checkEventInitiator(event, userId);

        if (event.getState().equals(StateEvent.PUBLISHED)) {
            throw new ConflictException(String.format("Событие не должно быть опубликовано, userId = %s, " +
                    "eventId = %s, updateEventUserRequest: %s.", userId, eventId, updateEventUserRequest));
        }
        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            Category category = utilService.returnCategory(updateEventUserRequest.getCategory());
            event.setCategory(category);
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            if (updateEventUserRequest.getEventDate().plusHours(2).isBefore(LocalDateTime.now())) {
                throw new BadRequestException(String.format("Дата и время на которые намечено событие " +
                        "не может быть раньше, чем через два часа от текущего момента, userId = %s, eventId = %s, " +
                        "updateEventUserRequest: %s.", userId, eventId, updateEventUserRequest));
            }
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.getLocation() != null) {
            Location location = utilService.returnLocation(updateEventUserRequest.getLocation());
            event.setLocation(location);
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getStateAction() != null) {
            if (!event.getState().equals(StateEvent.PENDING) && !event.getState().equals(StateEvent.CANCELED)) {
                throw new ConflictException(String.format("Изменить можно только отмененные события или события " +
                        "в состоянии ожидания модерации, userId = %s, eventId = %s, " +
                        "updateEventUserRequest: %s.", userId, eventId, updateEventUserRequest));
            }
            if (updateEventUserRequest.getStateAction().equals(StateActionUser.CANCEL_REVIEW)) {
                event.setState(StateEvent.CANCELED);
            }
            if (updateEventUserRequest.getStateAction().equals(StateActionUser.SEND_TO_REVIEW)) {
                event.setState(StateEvent.PENDING);
            }
        }
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }

        try {
            return EventMapper.toEventFullDto(eventRepository.saveAndFlush(event));
        } catch (DataIntegrityViolationException e) {
            throw new NotSaveException("Событие с id = " + eventId + ", userId = " + userId + ", " +
                    "не было обновлено: " + updateEventUserRequest);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getAllRequestsOfEventByUser(Long userId, Long eventId) {
        utilService.returnUser(userId);
        Event event = utilService.returnEvent(eventId);
        utilService.checkEventInitiator(event, userId);

        return ParticipationRequestMapper.convertParticipationRequestToDtoList(
                requestRepository.findAllByEventId(eventId));
    }

    @Override
    public EventRequestStatusUpdateResult updateAllRequestsOfEventByUser(
            Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        utilService.returnUser(userId);
        Event event = utilService.returnEvent(eventId);
        utilService.checkEventInitiator(event, userId);

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConflictException("У события лимит заявок равен 0 или отключена пре-модерация, userId = "
                    + userId + ", eventId = " + eventId + ", eventRequestStatusUpdateRequest: "
                    + eventRequestStatusUpdateRequest);
        }
        long confirmedLimit = requestRepository.countByEventIdAndStatus(eventId, StateRequest.CONFIRMED);
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= confirmedLimit) {
            throw new ConflictException("Нельзя подтвердить заявку, так как достигнут лимит по заявкам " +
                    "на данное событие, userId = " + userId + ", eventId = " + eventId +
                    ", eventRequestStatusUpdateRequest: " + eventRequestStatusUpdateRequest);
        }

        List<ParticipationRequest> requestsToUpdate = requestRepository
                .findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());
        List<ParticipationRequest> confirmed = new ArrayList<>();
        List<ParticipationRequest> rejected = new ArrayList<>();

        for (ParticipationRequest request : requestsToUpdate) {
            if (!request.getStatus().equals(StateRequest.PENDING)) {
                throw new ConflictException("Нельзя отменить уже принятую заявку на участие, userId = " + userId
                        + ", eventId = " + eventId + ", eventRequestStatusUpdateRequest: "
                        + eventRequestStatusUpdateRequest);
            }
            if (!request.getEvent().getId().equals(eventId)) {
                rejected.add(request);
                continue;
            }

            switch (eventRequestStatusUpdateRequest.getStatus()) {
                case CONFIRMED:
                    if (confirmedLimit < event.getParticipantLimit()) {
                        request.setStatus(StateRequest.CONFIRMED);
                        confirmedLimit++;
                        confirmed.add(request);
                    } else {
                        request.setStatus(StateRequest.REJECTED);
                        rejected.add(request);
                    }
                    break;

                case REJECTED:
                    request.setStatus(StateRequest.REJECTED);
                    rejected.add(request);
                    break;
            }
        }

        requestRepository.saveAll(requestsToUpdate);
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(ParticipationRequestMapper
                .convertParticipationRequestToDtoList(confirmed));
        result.setRejectedRequests(ParticipationRequestMapper
                .convertParticipationRequestToDtoList(rejected));

        return result;
    }

}