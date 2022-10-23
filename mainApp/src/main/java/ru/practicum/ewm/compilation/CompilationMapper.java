package ru.practicum.ewm.compilation;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CompilationMapper {
    public static Compilation toCompilation(CompilationDto compilationDto) {
        Compilation compilation = new Compilation();
        compilation.setCompilationId(compilationDto.getCompilationId());
        compilation.setTitle(compilationDto.getTitle());
        compilation.setPinned(compilationDto.getPinned());
        compilation.setEvents(compilationDto.getEvents());
        return compilation;
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setCompilationId(compilation.getCompilationId());
        compilationDto.setTitle(compilation.getTitle());
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setEvents(compilation.getEvents());
        return compilationDto;
    }

    public static NewCompilationDto toNewCompilationDto(Compilation compilation) {
        NewCompilationDto newCompilationDto = new NewCompilationDto();
        newCompilationDto.setTitle(compilation.getTitle());
        newCompilationDto.setPinned(compilation.getPinned());
        newCompilationDto.setEvents(compilation.getEvents());
        return newCompilationDto;
    }

    public static List<CompilationDto> toCompilationDtos(List<Compilation> compilations) {
        List<CompilationDto> dtos = new ArrayList<>();
        for (Compilation compilation : compilations) {
            dtos.add(toCompilationDto(compilation));
        }
        return dtos;
    }
}
