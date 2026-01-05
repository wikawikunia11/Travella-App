package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Long idPost;
    private String caption;
    private String description;
    private Double latitude;
    private Double longitude;
    private String visitDate;
    private List<PostImageDTO> images;
}
