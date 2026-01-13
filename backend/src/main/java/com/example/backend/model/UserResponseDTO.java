package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponseDTO {
    private String username;
    private String name;
    private String surname;
    private String biography;
    private String profilePic;
}