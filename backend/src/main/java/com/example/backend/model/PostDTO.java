package com.example.backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PostDTO {
    private Long idPost;
    private String caption;
    private String description;
    private Double latitude;
    private Double longitude;
    private LocalDate visitDate;
    private LocalDateTime postDate;
    private String username;
    private List<String> imageUrls;

    public PostDTO(Long idPost, String caption, String description, Double latitude, Double longitude,
                   LocalDate visitDate, LocalDateTime postDate, String username, List<String> imageUrls) {
        this.idPost = idPost;
        this.caption = caption;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.visitDate = visitDate;
        this.postDate = postDate;
        this.username = username;
        this.imageUrls = imageUrls;
    }

    // getter-y
    public Long getIdPost() { return idPost; }
    public String getCaption() { return caption; }
    public String getDescription() { return description; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public LocalDate getVisitDate() { return visitDate; }
    public LocalDateTime getPostDate() { return postDate; }
    public String getUsername() { return username; }
    public List<String> getImageUrls() { return imageUrls; }
}
