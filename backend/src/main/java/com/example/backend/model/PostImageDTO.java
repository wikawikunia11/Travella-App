package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostImageDTO {
    private Long id;
    private String fileName;
    private String filePath;
    private String contentType;
    private Long fileSize;
}

