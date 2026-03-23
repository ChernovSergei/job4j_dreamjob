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

import ru.job4j.dreamjob.controller.CandidateController;
import ru.job4j.dreamjob.controller.CandidateController;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.CandidateService;

public class CandidateControllerTest {

    private CandidateService candidateService;
    private CandidateController candidateController;
    private MultipartFile testFile;

    @BeforeEach
    public void initServices() {
        candidateService = mock(CandidateService.class);
        candidateController = new CandidateController(candidateService);
        testFile = new MockMultipartFile("testFile.img", new byte[] {1, 2, 3});
    }

    @Test
    public void whenRequestCandidateListPageThenGetPageWithCandidates() {
        var model = new ConcurrentModel();
        var candidate1 = new Candidate(1, "test1", "desc1", now(), 1);
        var candidate2 = new Candidate(2, "test2", "desc2", now(), 2);
        var expectedCandidates = List.of(candidate1, candidate2);
        when(candidateService.findAll()).thenReturn(expectedCandidates);

        var view = candidateController.getAll(model);
        var actualCandidates = model.getAttribute("candidates");

        assertThat(view).isEqualTo("candidates/list");
        assertThat(actualCandidates).isEqualTo(expectedCandidates);
    }

    @Test
    public void whenRequestCandidateCreationPageThenGetPageWithCities() {
        var view = candidateController.getCreationPage();
        assertThat(view).isEqualTo("candidates/create");
    }

    @Test   
    public void whenPostCandidateWithFileThenSameDataAndRedirectToCandidatesPage() throws Exception {
        var model = new ConcurrentModel();
        var candidate = new Candidate(1, "test1", "desc1", now(), 1);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.save(candidateArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(candidate);

        var view = candidateController.create(candidate, testFile, model);
        var actualCandidate = candidateArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidate).isEqualTo(candidate);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
    }

    @Test
    public void whenSomeExceptionThrownThenGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Failed to write file");
        when(candidateService.save(any(), any())).thenThrow(expectedException);

        var model = new ConcurrentModel();
        var view = candidateController.create(new Candidate(), testFile, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @Test 
    public void whenRequestCandidateOneThenGetPageWithCandidate() {
        var model = new ConcurrentModel();
        var candidate = new Candidate(1, "test1", "desc1", now(), 1);
        int id = 1;
        when(candidateService.findById(id)).thenReturn(Optional.of(candidate));
        
        var view = candidateController.getById(model, id);
        var actualCandidate = model.get("candidate");

        assertThat(view).isEqualTo("candidates/one");
        assertThat(actualCandidate).isEqualTo(candidate);
    }

    @Test 
    public void whenRequestedCandidateIsMissingThrowError() {

        var model = new ConcurrentModel();
        int id = 1;
        
        var view = candidateController.getById(model, id);
        var errorMessage = model.get("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(errorMessage).isEqualTo("Candidate wasn't found for such ID");
    }

    @Test   
    public void whenUpdateCandidateSuccessfullyThenGetPageWithCandidate() throws Exception {
        var candidate = new Candidate(1, "test1", "desc1", now(), 1);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var model = new ConcurrentModel();
        var candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.update(candidateArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(true);

        var view = candidateController.update(candidate, testFile, model);
        var actualCandidate = candidateArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidate).isEqualTo(candidate);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
    }

    @Test   
    public void whenUpdateCandidateUnsuccessfullyThenErrorMessage() throws Exception {
        var candidate = new Candidate(1, "test1", "desc1", now(), 1);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var model = new ConcurrentModel();
        var candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.update(candidateArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(false);

        var view = candidateController.update(candidate, testFile, model);
        var actualCandidate = candidateArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();
        var errorMessage = "Candidate wasn't found for such ID";

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualCandidate).isEqualTo(candidate);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
        assertThat(errorMessage).isEqualTo(model.getAttribute("message"));
    }

    @Test
    public void whenUpdateCandidateWithErrorThenGetErrorPageWithMessage() {
        var candidate = new Candidate(1, "test1", "desc1", now(), 1);
        var expectedException = new RuntimeException("Failed to update file");
        var model = new ConcurrentModel();
        when(candidateService.update(any(), any())).thenThrow(expectedException);

        var view = candidateController.update(candidate, testFile, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenDeleteCandidateThenReturnCandidatesPage() throws Exception {
        var model = new ConcurrentModel();
        int id = 1;
        when(candidateService.deleteById(id)).thenReturn(true);

        var view = candidateController.delete(model, id);

        assertThat(view).isEqualTo("redirect:/candidates");
    }

    @Test
    public void whenDeleteCandidateUnsuccessfulyThenGetErrors() throws Exception {
        var model = new ConcurrentModel();
        int id = 1;
        var errorMessage = "Candidate wasn't found for such ID";
        when(candidateService.deleteById(id)).thenReturn(false);

        var view = candidateController.delete(model, id);

        assertThat(view).isEqualTo("errors/404");
        assertThat(errorMessage).isEqualTo(model.getAttribute("message"));
    }

}
