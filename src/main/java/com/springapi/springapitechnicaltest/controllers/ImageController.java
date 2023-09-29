package com.springapi.springapitechnicaltest.controllers;

import com.springapi.springapitechnicaltest.domain.Image;
import com.springapi.springapitechnicaltest.services.ImageManagerService;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("${api.request.path}")
@RequiredArgsConstructor
@ApiResponses( value = {
        @ApiResponse(responseCode = "400", description = "Missing required fields or set types different than required",
                content = { @Content(schema = @Schema) }),
        @ApiResponse(responseCode = "403", description = "You do not have permission to do this", content = { @Content(schema = @Schema) })
})
public class ImageController {
    private final ImageManagerService imageManagerService;

    @ApiResponses( value = {
            @ApiResponse(responseCode = "201",
                    description = "Image saved successfully", headers = {
                    @Header(name = HttpHeaders.LOCATION, schema =
                    @Schema(type = "string"),
                            description = "Id of the image saved") },
                    content = { @Content(schema = @Schema) })
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "/upload/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String id = imageManagerService.saveFile(file);
        return ResponseEntity.created(URI.create(id)).build();
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "200",
                    description = "Image found and returned",
                    content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404",
                    description = "Image not found",
                    content = { @Content(schema = @Schema) })
    })
    @GetMapping("/download/image/{id}")
    public ResponseEntity<ByteArrayResource> download(@PathVariable String id) throws IOException {
        Image imageFile = imageManagerService.downloadFile(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imageFile.getFileType() ))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imageFile.getFilename() + "\"")
                .body(new ByteArrayResource(imageFile.getFile()));
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "204",
                    description = "Image deleted",
                    content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404",
                    description = "Image not found",
                    content = { @Content(schema = @Schema) })
    })
    @DeleteMapping("delete/image/{idImage}")
    public ResponseEntity<?> delete(@PathVariable String idImage) throws IOException {
        imageManagerService.deleteFile(idImage);
        System.out.println(idImage);
        return ResponseEntity.noContent().build();
    }
}
