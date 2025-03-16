package ga.banga.opencvtalk.service.dto;

import java.util.UUID;

public class PersonDTO {
    private UUID id;
    private String name;
    private String photoUrl;
    private Double distance;

    public PersonDTO() {
    }

    public PersonDTO(String name, String photoUrl) {
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    // Getters et setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
