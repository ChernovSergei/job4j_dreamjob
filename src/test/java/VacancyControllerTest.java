import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;

import ru.job4j.dreamjob.controller.VacancyController;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.VacancyService;

public class VacancyControllerTest {

    private VacancyService vacancyService;
    private CityService cityService;
    private VacancyController vacancyController;
    private MultipartFile testFile;

    @BeforeEach
    public void initServices() {
        vacancyService = mock(VacancyService.class);
        cityService = mock(CityService.class);
        vacancyController = new VacancyController(vacancyService, cityService);
        testFile = new MockMultipartFile("testFile.img", new byte[] {1, 2, 3});
    }

    @Test
    public void whenRequestVacancyListPageThenGetPageWithVacancies() {
        var vacancy1 = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var vacancy2 = new Vacancy(2, "test2", "desc2", now(), false, 3, 4);
        var expectedVacancies = List.of(vacancy1, vacancy2);
        when(vacancyService.findAll()).thenReturn(expectedVacancies);

        var model = new ConcurrentModel();
        var view = vacancyController.getAll(model);
        var actualVacancies = model.getAttribute("vacancies");

        assertThat(view).isEqualTo("vacancies/list");
        assertThat(actualVacancies).isEqualTo(expectedVacancies);
    }

    @Test
    public void whenRequestVacancyCreationPageThenGetPageWithCities() {
        var city1 = new City(1, "Moscow");
        var city2 = new City(2, "Saint Petersburg");
        var expectedCities = List.of(city1, city2);
        when(cityService.findAll()).thenReturn(expectedCities);

        var model = new ConcurrentModel();
        var view = vacancyController.getCreationPage(model);
        var actualVacancies = model.getAttribute("cities");

        assertThat(view).isEqualTo("vacancies/create");
        assertThat(actualVacancies).isEqualTo(expectedCities);
    }

    @Test   
    public void whenPostVacancyWithFileThenSameDataAndRedirectToVacanciesPage() throws Exception {
        var vacancy = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var vacancyArgumentCaptor = ArgumentCaptor.forClass(Vacancy.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(vacancyService.save(vacancyArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(vacancy);

        var model = new ConcurrentModel();
        var view = vacancyController.create(vacancy, testFile, model);
        var actualVacancy = vacancyArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualVacancy).isEqualTo(vacancy);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
    }

    @Test
    public void whenSomeExceptionThrownThenGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Failed to write file");
        when(vacancyService.save(any(), any())).thenThrow(expectedException);

        var model = new ConcurrentModel();
        var view = vacancyController.create(new Vacancy(), testFile, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @Test 
    public void whenRequestVacancyOneThenGetPageWithVacancy() {
        var model = new ConcurrentModel();
        var vacancy = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var city1 = new City(1, "Moscow");
        var city2 = new City(2, "Saint Petersburg");
        var expectedCities = List.of(city1, city2);
        int id = 1;
        when(vacancyService.findById(id)).thenReturn(Optional.of(vacancy));
        when(cityService.findAll()).thenReturn(expectedCities);
        
        var view = vacancyController.getById(model, id);
        var actualVacancy = model.get("vacancy");
        var actualCities = model.get("cities");

        assertThat(view).isEqualTo("vacancies/one");
        assertThat(actualCities).isEqualTo(expectedCities);
        assertThat(actualVacancy).isEqualTo(vacancy);
    }

    @Test 
    public void whenRequestedVacancyIsMissingThrowError() {

        var model = new ConcurrentModel();
        int id = 1;
        
        var view = vacancyController.getById(model, id);
        var errorMessage = model.get("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(errorMessage).isEqualTo("Vacancy wasn't found for such ID");
    }

    @Test   
    public void whenUpdateVacancySuccessfullyThenGetPageWithVacancy() throws Exception {
        var vacancy = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var model = new ConcurrentModel();
        var vacancyArgumentCaptor = ArgumentCaptor.forClass(Vacancy.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(vacancyService.update(vacancyArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(true);

        var view = vacancyController.update(vacancy, testFile, model);
        var actualVacancy = vacancyArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualVacancy).isEqualTo(vacancy);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
    }

    @Test   
    public void whenUpdateVacancyUnsuccessfullyThenErrorMessage() throws Exception {
        var vacancy = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var model = new ConcurrentModel();
        var vacancyArgumentCaptor = ArgumentCaptor.forClass(Vacancy.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(vacancyService.update(vacancyArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(false);

        var view = vacancyController.update(vacancy, testFile, model);
        var actualVacancy = vacancyArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();
        var errorMessage = "Vacancy wasn't found for such ID";

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualVacancy).isEqualTo(vacancy);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
        assertThat(errorMessage).isEqualTo(model.getAttribute("message"));
    }

    @Test
    public void whenUpdateVacancyWithErrorThenGetErrorPageWithMessage() {
        var vacancy = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var expectedException = new RuntimeException("Failed to update file");
        var model = new ConcurrentModel();
        when(vacancyService.update(any(), any())).thenThrow(expectedException);

        var view = vacancyController.update(vacancy, testFile, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenDeleteVacancyThenReturnVacanciesPage() throws Exception {
        var model = new ConcurrentModel();
        int id = 1;
        when(vacancyService.deleteById(id)).thenReturn(true);

        var view = vacancyController.delete(model, id);

        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenDeleteVacancyUnsuccessfulyThenGetErrors() throws Exception {
        var model = new ConcurrentModel();
        int id = 1;
        var errorMessage = "Vacancy wasn't found for such ID";
        when(vacancyService.deleteById(id)).thenReturn(false);

        var view = vacancyController.delete(model, id);

        assertThat(view).isEqualTo("errors/404");
        assertThat(errorMessage).isEqualTo(model.getAttribute("message"));
    }

}
