package ru.practicum.ewm.compilation;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.CompilationNotFoundException;

import java.util.List;

@Service
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;

    public CompilationServiceImpl(CompilationRepository compilationRepository) {
        this.compilationRepository = compilationRepository;
    }

    public List<CompilationDto> findCompilationsByPinned(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("compilationId"));
        return CompilationMapper.toCompilationDtos(compilationRepository
                .findAllComtilationsByPinnedState(pinned, pageable));
    }

    public CompilationDto findCompilationByCompilationId(Long compilationId) {
        return CompilationMapper.toCompilationDto(compilationRepository.findById(compilationId)
                .orElseThrow(() -> new CompilationNotFoundException("Compilation not found")));
    }
}
