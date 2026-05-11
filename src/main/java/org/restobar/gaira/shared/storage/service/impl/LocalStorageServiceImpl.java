package org.restobar.gaira.shared.storage.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.restobar.gaira.shared.storage.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
@ConditionalOnProperty(name = "app.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalStorageServiceImpl implements StorageService {

    private final Path rootLocation;

    public LocalStorageServiceImpl(@Value("${app.storage.local.path:uploads}") String uploadPath) {
        this.rootLocation = Paths.get(uploadPath);
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            log.error("No se pudo inicializar el directorio de almacenamiento", e);
        }
    }

    @Override
    public String upload(MultipartFile file, String folder) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("No se puede subir un archivo vacío");
            }

            String extension = getExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID().toString() + extension;
            
            Path destinationFolder = rootLocation.resolve(folder);
            Files.createDirectories(destinationFolder);

            Path destinationFile = destinationFolder.resolve(filename);
            
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
            
            return folder + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar archivo localmente", e);
        }
    }

    @Override
    public String getUrl(String filename) {
        // En local, asumimos que servimos la carpeta 'uploads' bajo /api/storage/
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/storage/")
                .path(filename)
                .toUriString();
    }

    @Override
    public void delete(String filename) {
        try {
            Files.deleteIfExists(rootLocation.resolve(filename));
        } catch (IOException e) {
            log.warn("No se pudo eliminar el archivo: {}", filename);
        }
    }

    @Override
    public org.springframework.core.io.Resource loadAsResource(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("No se pudo leer el archivo: " + filename);
            }
        } catch (java.net.MalformedURLException e) {
            throw new RuntimeException("No se pudo leer el archivo: " + filename, e);
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }
}
