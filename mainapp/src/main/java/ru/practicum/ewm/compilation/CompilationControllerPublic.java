package ru.practicum.ewm.compilation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/compilations")
public class CompilationControllerPublic {
    private final CompilationService compilationService;

    @Autowired
    public CompilationControllerPublic(CompilationServiceImpl compilationService) {
        this.compilationService = compilationService;
    }

    @GetMapping
    public List<CompilationDto> findCompilationsByPinned(@RequestParam(required = false) Boolean pinned,
                                                         @RequestParam(required = false,
                                                                 defaultValue = "0") Integer from,
                                                         @RequestParam(required = false,
                                                                 defaultValue = "10") Integer size) {
        return compilationService.findCompilationsByPinned(pinned, from, size);
    }

    @GetMapping("/{compilationId}")
    public CompilationDto findCompilationByCompilationId(@PathVariable Long compilationId) {
        return compilationService.findCompilationByCompilationId(compilationId);
    }
}
