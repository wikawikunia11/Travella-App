package com.example.backend.model;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.CascadeType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_post")
    private Long idPost;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String caption;
    private String description;
    private Double longitude;
    private Double latitude;

    @Column(name = "visit_date")
    private LocalDate visitDate;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<PostImage> images = new ArrayList<>();

    // country id to be added later

    @Column(name = "post_date", updatable = false)
    private LocalDateTime postDate;

    @PrePersist
    protected void onCreate() {
        if (this.postDate == null) {
            this.postDate = LocalDateTime.now();
        }
    }

}
