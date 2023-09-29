package com.springapi.springapitechnicaltest.controllers;

import com.springapi.springapitechnicaltest.configuration.SecurityConfiguration;
import com.springapi.springapitechnicaltest.domain.Image;
import com.springapi.springapitechnicaltest.services.ImageManagerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfiguration.class)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageManagerService imageManagerService;
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturnCreatedAndHeader_WhenUploadWithAdminRole() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-image.jpg", "image/jpeg", "Test Image Content".getBytes());

        when(imageManagerService.saveFile(file)).thenReturn("123");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/upload/image")
                        .file(file))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "123"));

        verify(imageManagerService, times(1)).saveFile(file);
    }

    @Test
    @WithMockUser
    void shouldReturnCreatedAndHeader_WhenUploadWithUserRole() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-image.jpg", "image/jpeg", "Test Image Content".getBytes());

        when(imageManagerService.saveFile(file)).thenReturn("123");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/upload/image")
                        .file(file))
                .andExpect(status().isForbidden());

        verify(imageManagerService, times(0)).saveFile(file);
    }

    @Test
    void shouldReturnCreatedAndHeader_WhenUploadWithNoAuth() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-image.jpg", "image/jpeg", "Test Image Content".getBytes());

        when(imageManagerService.saveFile(file)).thenReturn("123");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/upload/image")
                        .file(file))
                .andExpect(status().isForbidden());

        verify(imageManagerService, times(0)).saveFile(file);
    }

    @Test
    void shouldReturnImage_WhenDownloadAsAnyRole() throws Exception{
        String imageId = "123";

        Image image = new Image();
        image.setFileType("image/jpeg");
        image.setFilename("test-image.jpg");
        image.setFile("Test Image Content".getBytes());

        when(imageManagerService.downloadFile(eq(imageId)))
                .thenReturn(image);

        mockMvc.perform(get("/api/v1/download/image/{id}", imageId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("image/jpeg")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test-image.jpg\""))
                .andExpect(content().string("Test Image Content")); // Replace with your image content

        verify(imageManagerService, times(1)).downloadFile(eq(imageId));
    }
}