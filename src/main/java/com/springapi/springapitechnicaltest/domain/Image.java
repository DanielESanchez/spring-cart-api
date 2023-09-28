package com.springapi.springapitechnicaltest.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("images")
public class Image {
    private String filename;
    private String fileType;
    private String fileSize;
    private byte[] file;
}
