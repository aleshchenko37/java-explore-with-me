package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.model.SortSearch;
import ru.practicum.ewm.event.model.StateEvent;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventPublicService {

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> getAllEvents(
            String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
            Boolean onlyAvailable, SortSearch sort, Integer from, Integer size, HttpServletRequest request);

    List<EventFullDto> getAllEventsByAdmin(
            List<Long> users, List<StateEvent> states, List<Long> categories, LocalDateTime rangeStart,
            LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto getPublicEventById(Long eventId, HttpServletRequest request);

    List<EventShortDto> getAllEventsByLocation(
            Long locId, LocalDateTime rangeStart, LocalDateTime rangeEnd,
            Integer from, Integer size, HttpServletRequest request);

}