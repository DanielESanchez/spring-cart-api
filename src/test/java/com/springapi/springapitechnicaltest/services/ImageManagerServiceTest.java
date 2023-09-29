package com.springapi.springapitechnicaltest.services;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.springapi.springapitechnicaltest.controllers.NotFoundException;
import com.springapi.springapitechnicaltest.domain.Image;
import org.bson.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ImageManagerServiceTest {
    @InjectMocks
    private ImageManagerServiceImpl imageManagerService;

    @Mock
    private GridFsTemplate gridFsTemplate;

    @Mock
    private GridFsOperations gridFsOperations;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

//    @AfterEach
//    public void after() {
//        reset(gridFsTemplate);
//        reset(gridFsOperations);
//    }

    @Test
    void shouldReturnImage_WhenFoundInDB() throws IOException {
        BsonString objectId = new BsonString("123");
        Document metadata = new Document();
        metadata.put("_contentType", "image/jpeg");
        metadata.put("fileSize", 1024);
        GridFSFile gridFSFile = new GridFSFile(objectId, "sample.jpg", 1024, 0, new Date(), metadata);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("Sample image data".getBytes());
        GridFsResource gridFsResource = mock(GridFsResource.class);

        when(gridFsTemplate.findOne(any(Query.class))).thenReturn(gridFSFile);
        when(gridFsResource.getInputStream()).thenReturn(inputStream);
        when(gridFsOperations.getResource(gridFSFile)).thenReturn(gridFsResource);

        Image image = imageManagerService.downloadFile(objectId.toString());

        assertEquals("sample.jpg", image.getFilename());
        assertEquals("image/jpeg", image.getFileType());
        assertEquals("1024", image.getFileSize());
    }



}