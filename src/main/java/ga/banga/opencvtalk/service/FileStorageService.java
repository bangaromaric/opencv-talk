package ga.banga.opencvtalk.service;


import ga.banga.opencvtalk.config.ParamConfig;
import ga.banga.opencvtalk.exception.StorageException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path photoStorageLocation;
    private final ParamConfig storageConfig;

    public FileStorageService(ParamConfig storageConfig) {
        this.storageConfig = storageConfig;
        this.photoStorageLocation = Paths.get(storageConfig.getPhotoDirectory())
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.photoStorageLocation);
        } catch (Exception ex) {
            throw new StorageException("Impossible de créer le répertoire de stockage des photos", ex);
        }
    }

    public String storePhoto(MultipartFile file, UUID personId) {
        try {
            // Génération d'un nom unique pour éviter les collisions
            String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
            String fileName = personId.toString() + "." + fileExtension;

            // Normalisation du chemin pour éviter les attaques de type directory traversal
            Path targetLocation = this.photoStorageLocation.resolve(fileName);

            // Copie du fichier
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new StorageException("Échec du stockage du fichier", ex);
        }
    }

    public Resource loadPhotoAsResource(String fileName) {
        try {
            Path filePath = this.photoStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("Photo non trouvée: " + fileName);
            }
        } catch (Exception ex) {
            throw new StorageException("Impossible de charger la photo", ex);
        }
    }
}
