package ru.practicum.ewm.compilation;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.event.EventMapper;

import java.util.ArrayList;
import java.util.List;

@Component
public class CompilationMapper {

    public static Compilation toCompilationFromNew(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setPinned(newCompilationDto.getPinned());
        return compilation;
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilation.getId());
        compilationDto.setTitle(compilation.getTitle());
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setEvents(EventMapper.toEventShortDtos(compilation.getEvents()));
        return compilationDto;
    }

    public static List<CompilationDto> toCompilationDtos(List<Compilation> compilations) {
        List<CompilationDto> dtos = new ArrayList<>();
        for (Compilation compilation : compilations) {
            dtos.add(toCompilationDto(compilation));
        }
        return dtos;
    }
}
