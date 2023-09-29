package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.controllers.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ImageSaveServiceTest {
    @Autowired
    private ImageManagerServiceImpl imageManagerService;

    @Test
    void shouldReturnId_WhenSavedImage() throws Exception {
        byte[] content = "Sample file content".getBytes();
        MultipartFile multipartFile = new MockMultipartFile("file.txt", "file.txt", "text/plain", content);

        String fileId = imageManagerService.saveFile(multipartFile);
        imageManagerService.deleteFile(fileId);

        assertNotNull(fileId);

    }

    @Test
    void shouldReturnNotFound_WhenImageIsNotSavedInDB() {
        assertThrows(NotFoundException.class, ()-> imageManagerService.downloadFile("someId"));

    }
}
