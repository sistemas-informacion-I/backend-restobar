package org.restobar.gaira.shared.storage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StorageWebConfig implements WebMvcConfigurer {

    @Value("${app.storage.local.path:uploads}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(uploadPath);
        String uploadPathAbsolute = uploadDir.toFile().getAbsolutePath();
        
        // Permite acceder a los archivos locales vía /api/storage/filename
        registry.addResourceHandler("/api/storage/**")
                .addResourceLocations("file:" + uploadPathAbsolute + "/");
    }
}
