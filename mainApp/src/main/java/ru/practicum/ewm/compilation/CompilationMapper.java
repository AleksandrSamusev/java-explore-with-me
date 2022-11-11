package ru.practicum.ewm.compilation;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.event.Event;
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

    public static NewCompilationDto toNewCompilationDto(Compilation compilation) {
        NewCompilationDto newCompilationDto = new NewCompilationDto();
        newCompilationDto.setTitle(compilation.getTitle());
        newCompilationDto.setPinned(compilation.getPinned());

        List<Long> eventIds = new ArrayList<>();
        for (Event event : compilation.getEvents()) {
            eventIds.add(event.getId());
        }
        newCompilationDto.setEvents(eventIds);
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
