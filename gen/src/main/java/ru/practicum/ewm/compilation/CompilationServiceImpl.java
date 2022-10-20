package ru.practicum.ewm.compilation;

import org.springframework.stereotype.Service;

@Service
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;

    public CompilationServiceImpl(CompilationRepository compilationRepository) {
        this.compilationRepository = compilationRepository;
    }
}
