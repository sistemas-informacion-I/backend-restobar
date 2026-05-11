package org.restobar.gaira.shared.storage.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    /**
     * Sube un archivo al almacenamiento configurado.
     * @param file El archivo a subir.
     * @param folder Carpeta o prefijo dentro del almacenamiento.
     * @return El nombre del archivo almacenado o la ruta relativa.
     */
    String upload(MultipartFile file, String folder);

    /**
     * Obtiene la URL pública o accesible para mostrar la imagen.
     * @param filename El nombre del archivo o ruta relativa obtenida al subir.
     * @return La URL completa.
     */
    String getUrl(String filename);

    /**
     * Elimina un archivo del almacenamiento.
     * @param filename El nombre del archivo a eliminar.
     */
    void delete(String filename);

    /**
     * Carga un archivo como recurso para ser servido por el controlador.
     * @param filename El nombre del archivo o ruta relativa.
     * @return El recurso cargado.
     */
    org.springframework.core.io.Resource loadAsResource(String filename);
}
