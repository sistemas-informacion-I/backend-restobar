package org.restobar.gaira.shared.storage.controller;

import lombok.RequiredArgsConstructor;
import org.restobar.gaira.shared.storage.service.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "products") String folder) {
        
        String filename = storageService.upload(file, folder);
        String url = storageService.getUrl(filename);
        
        return ResponseEntity.ok(Map.of(
            "filename", filename,
            "url", url
        ));
    }

    @DeleteMapping("/{folder}/{filename}")
    public ResponseEntity<?> deleteFile(
            @PathVariable String folder,
            @PathVariable String filename) {
        
        storageService.delete(folder + "/" + filename);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{folder}/{filename}")
    public ResponseEntity<org.springframework.core.io.Resource> getFile(
            @PathVariable String folder,
            @PathVariable String filename) {
        
        org.springframework.core.io.Resource file = storageService.loadAsResource(folder + "/" + filename);
        
        String contentType = "application/octet-stream";
        try {
            contentType = java.nio.file.Files.probeContentType(java.nio.file.Paths.get(file.getURI()));
        } catch (java.io.IOException ex) {
            // Log error
        }

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, contentType)
                .body(file);
    }
}
