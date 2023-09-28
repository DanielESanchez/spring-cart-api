package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.domain.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageManagerService {
    Image downloadFile(String id, Image imageFile) throws IOException;
    String saveFile(MultipartFile upload) throws IOException;
}
