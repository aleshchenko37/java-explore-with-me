package ru.practicum.ewm.compilation.mapper;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;

import java.util.List;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static Compilation toCompilationFromNewDto(NewCompilationDto dto, List<Event> events) {
        return Compilation.builder()
                .events(events)
                .pinned(dto.getPinned())
                .title(dto.getTitle())
                .build();
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        CompilationDto compilationDto = new CompilationDto();
        if (compilation != null) {
            compilationDto.setId(compilation.getId());
            compilationDto.setPinned(compilation.getPinned());
            compilationDto.setTitle(compilation.getTitle());
            if (compilation.getEvents() != null) {
                compilationDto.setEvents(EventMapper.convertEventListToEventShortDtoList((compilation.getEvents())));
            }
        }
        return compilationDto;
    }

    public static List<CompilationDto> toDtoList(List<Compilation> compilations) {
        return compilations.stream().map(CompilationMapper::toCompilationDto).collect(Collectors.toList());
    }
}