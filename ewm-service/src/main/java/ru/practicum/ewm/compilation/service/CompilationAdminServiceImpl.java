package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.NotSaveException;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CompilationAdminServiceImpl implements CompilationAdminService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto saveCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = new ArrayList<>();
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            events.addAll(eventRepository.findAllById(newCompilationDto.getEvents()));
        }

        try {
            Compilation compilation = compilationRepository.save(
                    CompilationMapper.toCompilationFromNewDto(newCompilationDto, events));
            return CompilationMapper.toCompilationDto(compilation);
        } catch (DataIntegrityViolationException e) {
            throw new NotSaveException("Подборка событий не была создана: " + newCompilationDto);
        }
    }

    @Override
    public void deleteCompilationById(Long compId) {
        returnCompilation(compId);
        try {
            compilationRepository.deleteById(compId);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Подборка с ид = " + compId + " не может быть удалена.");
        }
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = returnCompilation(compId);

        if (updateCompilationRequest.getEvents() != null) {
            List<Event> events = new ArrayList<>(eventRepository.findAllById(updateCompilationRequest.getEvents()));
            compilation.setEvents(events);
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }

        try {
            return CompilationMapper.toCompilationDto(compilationRepository.saveAndFlush(compilation));
        } catch (DataIntegrityViolationException e) {
            throw new NotSaveException("Подборка событий с id = " + compId +
                    " не была обновлена: " + updateCompilationRequest);
        }
    }

    private Compilation returnCompilation(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Подборка событий с идентификатором " + compId + " не найдена."));
    }

}