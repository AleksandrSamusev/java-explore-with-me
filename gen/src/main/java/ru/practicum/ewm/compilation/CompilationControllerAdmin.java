package ru.practicum.ewm.compilation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompilationControllerAdmin {
    private final CompilationServiceImpl compilationService;

    @Autowired
    public CompilationControllerAdmin(CompilationServiceImpl compilationService) {
        this.compilationService = compilationService;
    }
}
