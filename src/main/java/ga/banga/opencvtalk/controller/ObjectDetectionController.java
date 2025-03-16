package ga.banga.opencvtalk.controller;

import ga.banga.opencvtalk.model.ObjectDetection;
import ga.banga.opencvtalk.service.FileStorageService;
import ga.banga.opencvtalk.service.ObjectDetectionService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/detection")
@CrossOrigin(origins = "*")
public class ObjectDetectionController {

    private final ObjectDetectionService service;
    private final FileStorageService fileStorageService;

    public ObjectDetectionController(ObjectDetectionService service, FileStorageService fileStorageService) {
        this.service = service;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ObjectDetection> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            ObjectDetection detection = service.detectAndSave(file);
            return ResponseEntity.ok(detection);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/search")
    public ResponseEntity<List<ObjectDetection>> findSimilar(@RequestBody float[] embedding,
                                                             @RequestParam(defaultValue = "10") int limit) {
        List<ObjectDetection> similar = service.findSimilarObjects(embedding, limit);
        return ResponseEntity.ok(similar);
    }

    @GetMapping(value = "/image/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Resource> getImage(@PathVariable UUID id) {
        try {
            ObjectDetection detection = service.findById(id);
            if (detection == null) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = fileStorageService.loadPhotoAsResource(detection.getImagePath());
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
} 