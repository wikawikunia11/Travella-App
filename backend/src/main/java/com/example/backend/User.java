package com.example.backend;

public class User {
    private Long id;
    private String name;
    private String nickname;
    private String email;
    private String biography;

    public User(Long id, String name, String nickname, String email, String biography) {
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.biography = biography;
    }

    // gettery i settery
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getNickname() { return nickname; }
    public String getEmail() { return email; }
    public String getBiography() { return biography; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setEmail(String email) { this.email = email; }
    public void setBiography(String biography) { this.biography = biography; }
}
