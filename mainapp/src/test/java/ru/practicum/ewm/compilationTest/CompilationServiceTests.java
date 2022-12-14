package ru.practicum.ewm.compilationTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.ewm.compilation.Compilation;
import ru.practicum.ewm.compilation.CompilationDto;
import ru.practicum.ewm.compilation.CompilationService;
import ru.practicum.ewm.compilation.NewCompilationDto;
import ru.practicum.ewm.exception.CompilationNotFoundException;
import ru.practicum.ewm.exception.InvalidParameterException;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=ewm",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)

public class CompilationServiceTests<T extends CompilationService> {

    private final EntityManager em;
    private final CompilationService compilationService;

    @Test
    public void givenValidDto_WhenCreateCompilation_ThenCompilationCreated() {

        NewCompilationDto comp = new NewCompilationDto(List.of(), "compilation", false);
        compilationService.createCompilation(comp);

        List<Compilation> compilationsFromDb = em.createQuery("select c from Compilation c",
                Compilation.class).getResultList();

        assertThat(compilationsFromDb.size(), equalTo(1));
        assertThat(compilationsFromDb.get(0).getTitle(), equalTo("compilation"));
        assertThat(compilationsFromDb.get(0).getEvents().size(), equalTo(0));
    }

    @Test
    public void givenDtoWithTitleIsNull_WhenCreateCompilation_ThenException() {

        NewCompilationDto comp = new NewCompilationDto(List.of(), null, false);

        assertThrows(InvalidParameterException.class,
                () -> compilationService.createCompilation(comp));
    }

    @Test
    public void givenDtoWithTitleIsBlank_WhenCreateCompilation_ThenException() {

        NewCompilationDto comp = new NewCompilationDto(List.of(), "   ", false);

        assertThrows(InvalidParameterException.class,
                () -> compilationService.createCompilation(comp));
    }

    @Test
    public void givenIdExists_WhenDeleteCompilationById_ThenCompilationDeleted() {

        NewCompilationDto comp = new NewCompilationDto(List.of(), "compilation", false);
        compilationService.createCompilation(comp);

        compilationService.deleteCompilationById(1L);

        assertThat(em.createQuery("select c from Compilation c",
                Compilation.class).getResultList().size(), equalTo(0));
    }

    @Test
    public void givenIdNotExists_WhenDeleteCompilationById_ThenException() {

        NewCompilationDto comp = new NewCompilationDto(List.of(), "compilation", false);
        compilationService.createCompilation(comp);

        assertThrows(CompilationNotFoundException.class,
                () -> compilationService.deleteCompilationById(999L));
    }

    @Test
    public void givenIdExists_WhenFindCompilationById_ThenCompilationFound() {

        NewCompilationDto comp = new NewCompilationDto(List.of(), "compilation", false);
        compilationService.createCompilation(comp);

        assertThat(compilationService.findCompilationByCompilationId(1L).getTitle(),
                equalTo("compilation"));
    }

    @Test
    public void givenIdNotExists_WhenFindCompilationById_ThenException() {

        NewCompilationDto comp = new NewCompilationDto(List.of(), "compilation", false);
        compilationService.createCompilation(comp);

        assertThrows(CompilationNotFoundException.class,
                () -> compilationService.findCompilationByCompilationId(999L));
    }

    @Test
    public void givenPinnedIsTrue_WhenFindByPinned_ThenReturnListPinned() {

        NewCompilationDto pinned = new NewCompilationDto(List.of(), "pinned_compilation", true);
        NewCompilationDto unpinned = new NewCompilationDto(List.of(), "unpinned_compilation", false);
        compilationService.createCompilation(pinned);
        compilationService.createCompilation(unpinned);

        List<CompilationDto> compilations = compilationService.findCompilationsByPinned(true,
                0, 10);

        assertThat(compilations.size(), equalTo(1));
        assertThat(compilations.get(0).getTitle(), equalTo("pinned_compilation"));
        assertThat(compilations.get(0).getPinned(), equalTo(Boolean.TRUE));
    }

    @Test
    public void givenPinnedIsFalse_WhenFindByPinned_ThenReturnListUnpinned() {

        NewCompilationDto pinned = new NewCompilationDto(List.of(), "pinned_compilation", true);
        NewCompilationDto unpinned = new NewCompilationDto(List.of(), "unpinned_compilation", false);
        compilationService.createCompilation(pinned);
        compilationService.createCompilation(unpinned);

        List<CompilationDto> compilations = compilationService.findCompilationsByPinned(false,
                0, 10);

        assertThat(compilations.size(), equalTo(1));
        assertThat(compilations.get(0).getTitle(), equalTo("unpinned_compilation"));
        assertThat(compilations.get(0).getPinned(), equalTo(Boolean.FALSE));
    }

    @Test
    public void givenPinnedIsNull_WhenFindByPinned_ThenReturnListAll() {

        NewCompilationDto pinned = new NewCompilationDto(List.of(), "pinned_compilation", true);
        NewCompilationDto unpinned = new NewCompilationDto(List.of(), "unpinned_compilation", false);
        compilationService.createCompilation(pinned);
        compilationService.createCompilation(unpinned);

        List<CompilationDto> compilations = compilationService.findCompilationsByPinned(null,
                0, 10);

        assertThat(compilations.size(), equalTo(2));
    }

    @Test
    public void givenIdExist_WhenUnpinCompilation_ThenCompilationUnpinned() {

        NewCompilationDto pinned = new NewCompilationDto(List.of(), "pinned_compilation", true);
        compilationService.createCompilation(pinned);

        compilationService.unpinCompilation(1L);

        assertThat(em.createQuery("select c from Compilation c where" +
                " c.pinned IS true", Compilation.class).getResultList().size(), equalTo(0));
    }

    @Test
    public void givenIdNotExist_WhenUnpinCompilation_ThenException() {

        NewCompilationDto pinned = new NewCompilationDto(List.of(), "pinned_compilation", true);
        compilationService.createCompilation(pinned);

        assertThrows(CompilationNotFoundException.class,
                () -> compilationService.unpinCompilation(999L));
    }

    @Test
    public void givenIdExist_WhenPinCompilation_ThenCompilationPinned() {

        NewCompilationDto unpinned = new NewCompilationDto(List.of(), "unpinned_compilation", false);
        compilationService.createCompilation(unpinned);

        compilationService.pinCompilation(1L);

        assertThat(em.createQuery("select c from Compilation c where" +
                " c.pinned IS false", Compilation.class).getResultList().size(), equalTo(0));
    }

    @Test
    public void givenIdNotExist_WhenPinCompilation_ThenException() {

        NewCompilationDto unpinned = new NewCompilationDto(List.of(), "unpinned_compilation", false);
        compilationService.createCompilation(unpinned);

        assertThrows(CompilationNotFoundException.class,
                () -> compilationService.pinCompilation(999L));

    }
}
