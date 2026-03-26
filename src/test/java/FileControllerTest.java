import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import ru.job4j.dreamjob.controller.FileController;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;

public class FileControllerTest {

    private FileService fileService;
    private FileController fileController;
    private MultipartFile testFile;
    private ResponseEntity<?> responseEntity;

    @BeforeEach
    public void initServices() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
        testFile = new MockMultipartFile("testFile.img", new byte[] {1, 2, 3});
    }

    @Test
    public void whenFileIsFoundThenReturnOk() throws Exception {
        int id = 1;
        var resultFileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        when(fileService.getFileById(id)).thenReturn(Optional.of(resultFileDto));
            
        responseEntity = fileController.getById(id);

        assertThat(resultFileDto.getContent()).isEqualTo(responseEntity.getBody());
        assertThat(HttpStatus.OK).isEqualTo(responseEntity.getStatusCode());
    }

    @Test
    public void whenFileWasntFoundThenResponseNotFound() throws Exception {
        int id = 1;
        when(fileService.getFileById(id)).thenReturn(Optional.empty());
            
        responseEntity = fileController.getById(id);

        assertThat(HttpStatus.NOT_FOUND).isEqualTo(responseEntity.getStatusCode());
    }

}
