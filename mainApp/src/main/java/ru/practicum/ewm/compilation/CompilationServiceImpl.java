package ru.practicum.ewm.compilation;

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
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    public CompilationServiceImpl(CompilationRepository compilationRepository, EventRepository eventRepository) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
    }

    public List<CompilationDto> findCompilationsByPinned(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("compilationId"));
        return CompilationMapper.toCompilationDtos(compilationRepository
                .findAllCompilationsByPinnedState(pinned, pageable));
    }

    public CompilationDto findCompilationByCompilationId(Long compilationId) {
        return CompilationMapper.toCompilationDto(compilationRepository.findById(compilationId)
                .orElseThrow(() -> new CompilationNotFoundException("Compilation not found")));
    }

    public NewCompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        validateNewCompilationDto(newCompilationDto);
        Compilation tempCompilation = CompilationMapper.toCompilationFromNew(newCompilationDto);
        for (Long id : newCompilationDto.getIds()) {
            tempCompilation.getEvents().add(eventRepository.getReferenceById(id));
        }
        return CompilationMapper.toNewCompilationDto(compilationRepository.save(tempCompilation));
    }

    public void deleteCompilationById(Long compilationId) {
        validateCompilationId(compilationId);
        compilationRepository.deleteById(compilationId);
    }

    public void deleteEventFromCompilation(Long compilationId, Long eventId) {
        validateCompilationId(compilationId);
        validateEventId(eventId);
        checkIfEventInCompilation(compilationId, eventId);
        Compilation tempCompilation = compilationRepository.getReferenceById(compilationId);
        List<Event> tempEvents = tempCompilation.getEvents();
        int index = 0;
        for (Event event : tempEvents) {
            if (!Objects.equals(event.getId(), eventId)) {
                index++;
            } else {
                tempEvents.remove(index);
            }
        }
        tempCompilation.setEvents(tempEvents);
        compilationRepository.save(tempCompilation);
    }

    public void addEventToCompilation(Long compilationId, Long eventId) {
        validateCompilationId(compilationId);
        validateEventId(eventId);
        Compilation tempCompilation = compilationRepository.getReferenceById(compilationId);
        List<Event> tempEvents = tempCompilation.getEvents();
        Event tempEvent = eventRepository.getReferenceById(eventId);
        tempEvents.add(tempEvent);
        tempCompilation.setEvents(tempEvents);
        compilationRepository.save(tempCompilation);

    }


    private void validateNewCompilationDto(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getIds() == null) {
            throw new InvalidParameterException("Empty ids list");
        }
        for (Long id : newCompilationDto.getIds()) {
            if (!eventRepository.existsById(id)) {
                throw new CompilationNotFoundException("Compilation not found");
            }
        }
        if (newCompilationDto.getTitle() == null || newCompilationDto.getTitle().isBlank()) {
            throw new InvalidParameterException("Title parameter is not valid");
        }
    }

    private void validateCompilationId(Long compilationId) {
        if (!compilationRepository.existsById(compilationId)) {
            throw new CompilationNotFoundException("Compilation not found");
        }
    }

    private void validateEventId(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Event not found");
        }
    }

    private void checkIfEventInCompilation(Long compilationId, Long eventId) {
        List<Long> ids = new ArrayList<>();
        for (Event event : compilationRepository.getReferenceById(compilationId).getEvents()) {
            ids.add(event.getId());
        }
        if (!ids.contains(eventId)) {
            throw new EventNotFoundException("Event not in compilation");
        }
    }

    public void unpinCompilation(Long compilationId) {
        validateCompilationId(compilationId);
        Compilation tempCompilation = compilationRepository.getReferenceById(compilationId);
        tempCompilation.setPinned(false);
        compilationRepository.save(tempCompilation);
    }

    public void pinCompilation(Long compilationId) {
        validateCompilationId(compilationId);
        Compilation tempCompilation = compilationRepository.getReferenceById(compilationId);
        tempCompilation.setPinned(true);
        compilationRepository.save(tempCompilation);
    }
}
