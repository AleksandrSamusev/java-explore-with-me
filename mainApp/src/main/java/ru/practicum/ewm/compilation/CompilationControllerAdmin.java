package ru.practicum.ewm.compilation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/admin/compilations")
public class CompilationControllerAdmin {
    private final CompilationServiceImpl compilationService;

    @Autowired
    public CompilationControllerAdmin(CompilationServiceImpl compilationService) {
        this.compilationService = compilationService;
    }

    @PostMapping
    public NewCompilationDto createCompilation(@RequestBody NewCompilationDto newCompilationDto) {
        return compilationService.createCompilation(newCompilationDto);
    }

    @DeleteMapping("/{compilationId}")
    public void deleteCompilationById(@PathVariable Long compilationId) {
        compilationService.deleteCompilationById(compilationId);
    }

    @DeleteMapping("/{compilationId}/events/{eventId}")
    public void deleteEventFromCompilation(@PathVariable Long compilationId,
                                           @PathVariable Long eventId) {
        compilationService.deleteEventFromCompilation(compilationId, eventId);
    }

    @PatchMapping("/{compilationId}/events/{eventId}")
    public void addEventToCompilation(@PathVariable Long compilationId,
                                      @PathVariable Long eventId) {
        compilationService.addEventToCompilation(compilationId, eventId);
    }

    @DeleteMapping("/{compilationId}/pin")
    public void unpinCompilation(@PathVariable Long compilationId) {
        compilationService.unpinCompilation(compilationId);
    }

    @PatchMapping("/{compilationId}/pin")
    public void pinCompilation(@PathVariable Long compilationId) {
        compilationService.pinCompilation(compilationId);
    }
}
