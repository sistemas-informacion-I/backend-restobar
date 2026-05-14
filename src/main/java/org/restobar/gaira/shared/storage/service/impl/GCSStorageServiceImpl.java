package org.restobar.gaira.shared.storage.service.impl;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.extern.slf4j.Slf4j;
import org.restobar.gaira.shared.storage.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.UUID;

/**
 * Implementación funcional para Google Cloud Storage.
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "app.storage.type", havingValue = "gcs")
public class GCSStorageServiceImpl implements StorageService {

    @Value("${app.storage.gcs.bucket-name:}")
    private String bucketName;

    private Storage storage;

    @PostConstruct
    public void init() {
        // Inicializa el cliente de Storage usando las credenciales por defecto (ADC).
        // En producción (ej. GKE o Cloud Run) tomará automáticamente los permisos de la cuenta de servicio.
        try {
            this.storage = StorageOptions.getDefaultInstance().getService();
            log.info("Cliente de Google Cloud Storage inicializado correctamente.");
        } catch (Exception e) {
            log.warn("No se pudo inicializar el cliente de GCS (ignorar si solo usas almacenamiento local).", e);
        }
    }

    @Override
    public String upload(MultipartFile file, String folder) {
        if (storage == null) {
            throw new IllegalStateException("El cliente de GCS no está inicializado.");
        }
        
        try {
            log.info("Subiendo archivo a GCS: {}/{}", bucketName, folder);
            String filename = folder + "/" + UUID.randomUUID().toString() + getExtension(file.getOriginalFilename());
            
            BlobId blobId = BlobId.of(bucketName, filename);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();
            
            storage.create(blobInfo, file.getBytes());
            
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Error al subir archivo a GCS", e);
        }
    }

    @Override
    public String getUrl(String filename) {
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, filename);
    }

    @Override
    public void delete(String filename) {
        if (storage == null) {
            log.warn("Intento de eliminar en GCS pero el cliente no está inicializado: {}", filename);
            return;
        }
        
        log.info("Eliminando archivo de GCS: {}", filename);
        storage.delete(BlobId.of(bucketName, filename));
    }

    @Override
    public org.springframework.core.io.Resource loadAsResource(String filename) {
        if (storage == null) {
            throw new IllegalStateException("El cliente de GCS no está inicializado.");
        }
        com.google.cloud.storage.Blob blob = storage.get(BlobId.of(bucketName, filename));
        if (blob == null || !blob.exists()) {
            throw new RuntimeException("No se encontró el archivo en GCS: " + filename);
        }
        return new org.springframework.core.io.ByteArrayResource(blob.getContent());
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }
}
