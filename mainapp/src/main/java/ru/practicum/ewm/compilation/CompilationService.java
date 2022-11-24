package ru.practicum.ewm.compilation;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> findCompilationsByPinned(Boolean pinned, Integer from, Integer size);

    CompilationDto findCompilationByCompilationId(Long compilationId);

    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilationById(Long compilationId);

    void deleteEventFromCompilation(Long compilationId, Long eventId);

    void addEventToCompilation(Long compilationId, Long eventId);

    void unpinCompilation(Long compilationId);

    void pinCompilation(Long compilationId);
}
