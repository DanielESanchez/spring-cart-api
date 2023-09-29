package com.springapi.springapitechnicaltest.services;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.springapi.springapitechnicaltest.controllers.NotFoundException;
import com.springapi.springapitechnicaltest.domain.Image;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@Slf4j
@Service
@AllArgsConstructor
public class ImageManagerServiceImpl implements ImageManagerService {
    private final GridFsTemplate gridFsTemplate;
    private final GridFsOperations gridFsOperations;

    @Override
    public Image downloadFile(String id) throws IOException {
        Image imageFile = new Image();
        GridFSFile gridFSFile = gridFsTemplate.findOne( new Query(Criteria.where("_id").is(id)) );
        if(gridFSFile == null || gridFSFile.getMetadata() == null){
            throw new NotFoundException("Image not found");
        }
        imageFile.setFilename( gridFSFile.getFilename() );
        imageFile.setFileType( gridFSFile.getMetadata().get("_contentType").toString() );
        imageFile.setFileSize( gridFSFile.getMetadata().get("fileSize").toString() );
        imageFile.setFile( IOUtils.toByteArray(gridFsOperations.getResource(gridFSFile).getInputStream()) );
        return imageFile;
    }

    @Override
    public String saveFile(MultipartFile upload) throws IOException {
        DBObject metadata = new BasicDBObject();
        metadata.put("fileSize", upload.getSize());
        Object fileID = gridFsTemplate.store(upload.getInputStream(), upload.getOriginalFilename(), upload.getContentType(), metadata);
        log.info(new Date() + " File with name " + upload.getOriginalFilename() + " was saved");
        return fileID.toString();
    }

    @Override
    public void deleteFile(String id) {
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
        if(gridFSFile == null) throw new NotFoundException("File not found");
        gridFsTemplate.delete(new Query(Criteria.where("_id").is(id)));
        gridFsOperations.delete(new Query(Criteria.where("filename").is(gridFSFile.getFilename())));
        log.warn(new Date() + " File with name " + gridFSFile.getFilename() + " was deleted");
    }

}
