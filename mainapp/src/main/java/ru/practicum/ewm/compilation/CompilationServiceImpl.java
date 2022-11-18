package ru.practicum.ewm.compilation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.CompilationNotFoundException;
import ru.practicum.ewm.exception.EventNotFoundException;
import ru.practicum.ewm.exception.InvalidParameterException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    public CompilationServiceImpl(CompilationRepository compilationRepository, EventRepository eventRepository) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public List<CompilationDto> findCompilationsByPinned(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        if (pinned != null) {
            return CompilationMapper.toCompilationDtos(compilationRepository
                    .findAllCompilationsByPinnedState(pinned, pageable));
        } else {
            return CompilationMapper.toCompilationDtos(compilationRepository.findAll());
        }
    }

    @Override
    public CompilationDto findCompilationByCompilationId(Long compilationId) {
        return CompilationMapper.toCompilationDto(compilationRepository.findById(compilationId)
                .orElseThrow(() -> new CompilationNotFoundException("Compilation not found")));
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        validateNewCompilationDto(newCompilationDto);
        Compilation tempCompilation = CompilationMapper.toCompilationFromNew(newCompilationDto);
        List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());
        tempCompilation.setEvents(events);
        log.info("New compilation created");
        return CompilationMapper.toCompilationDto(compilationRepository.save(tempCompilation));
    }

    @Override
    public void deleteCompilationById(Long compilationId) {
        validateCompilationId(compilationId);
        log.info("Compilation with id = {} deleted", compilationId);
        compilationRepository.deleteById(compilationId);
    }

    @Override
    public void deleteEventFromCompilation(Long compilationId, Long eventId) {
        validateCompilationId(compilationId);
        validateEventId(eventId);
        checkIfEventInCompilation(compilationId, eventId);
        Compilation tempCompilation = compilationRepository.getReferenceById(compilationId);
        List<Event> tempEvents = tempCompilation.getEvents();
        for (int i = 0; i < tempEvents.size(); i++) {
            Event event = tempEvents.get(i);
            if (Objects.equals(event.getId(), eventId)) {
                tempEvents.remove(i);
            }
        }
        tempCompilation.setEvents(tempEvents);
        log.info("Event with id = {} was deleted from compilation with id = {}", eventId, compilationId);
        compilationRepository.save(tempCompilation);
    }

    @Override
    public void addEventToCompilation(Long compilationId, Long eventId) {
        validateCompilationId(compilationId);
        validateEventId(eventId);
        Compilation tempCompilation = compilationRepository.getReferenceById(compilationId);
        List<Event> tempEvents = tempCompilation.getEvents();
        Event tempEvent = eventRepository.getReferenceById(eventId);
        tempEvents.add(tempEvent);
        tempCompilation.setEvents(tempEvents);
        log.info("Event with id = {} was added to compilation with id = {}", eventId, compilationId);
        compilationRepository.save(tempCompilation);

    }

    @Override
    public void unpinCompilation(Long compilationId) {
        validateCompilationId(compilationId);
        Compilation tempCompilation = compilationRepository.getReferenceById(compilationId);
        tempCompilation.setPinned(false);
        log.info("Compilation with id = {} was unpinned", compilationId);
        compilationRepository.save(tempCompilation);
    }

    @Override
    public void pinCompilation(Long compilationId) {
        validateCompilationId(compilationId);
        Compilation tempCompilation = compilationRepository.getReferenceById(compilationId);
        tempCompilation.setPinned(true);
        log.info("Compilation with id = {} was pinned", compilationId);
        compilationRepository.save(tempCompilation);
    }

    private void validateNewCompilationDto(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getTitle() == null || newCompilationDto.getTitle().isBlank()) {
            log.info("Mandatory field 'title' is not valid");
            throw new InvalidParameterException("Title parameter is not valid");
        }
    }

    private void validateCompilationId(Long compilationId) {
        if (!compilationRepository.existsById(compilationId)) {
            log.info("Compilation with id = {} was not found", compilationId);
            throw new CompilationNotFoundException("Compilation not found");
        }
    }

    private void validateEventId(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            log.info("Event with id = {} was not found", eventId);
            throw new EventNotFoundException("Event not found");
        }
    }

    private void checkIfEventInCompilation(Long compilationId, Long eventId) {
        List<Long> ids = new ArrayList<>();
        for (Event event : compilationRepository.getReferenceById(compilationId).getEvents()) {
            ids.add(event.getId());
        }
        if (!ids.contains(eventId)) {
            log.info("Event with id = {} not was not found in compilation with id = {}", eventId, compilationId);
            throw new EventNotFoundException("Event not in compilation");
        }
    }
}
