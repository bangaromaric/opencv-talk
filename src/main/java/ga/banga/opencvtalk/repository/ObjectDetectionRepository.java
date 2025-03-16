package ga.banga.opencvtalk.repository;

import ga.banga.opencvtalk.model.ObjectDetection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ObjectDetectionRepository extends JpaRepository<ObjectDetection, UUID> {
    @Query(value = "SELECT * FROM object_detections ORDER BY embedding <-> CAST(:embedding AS vector) LIMIT :limit", nativeQuery = true)
    List<ObjectDetection> findSimilarObjects(@Param("embedding") float[] embedding, @Param("limit") int limit);
} 