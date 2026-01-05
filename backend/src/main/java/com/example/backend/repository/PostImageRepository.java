package com.example.backend.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import com.example.backend.model.PostImage;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    List<PostImage> findByPost_IdPost(Long postId);
    Optional<PostImage> findByPost_IdPostAndFileName(Long postId, String fileName);
}
