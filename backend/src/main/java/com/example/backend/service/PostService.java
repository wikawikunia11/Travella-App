package com.example.backend.service;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.example.backend.model.Post;
import com.example.backend.model.PostDTO;
import com.example.backend.model.PostImage;
import com.example.backend.model.PostImageDTO;
import com.example.backend.model.User;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;
import jakarta.transaction.Transactional;


@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public List<PostDTO> getAllPostsDTO() {
        return postRepository.findAll().stream().map(post -> {
            List<PostImageDTO> images = post.getImages().stream()
                .map(img -> new PostImageDTO(
                    img.getId(),
                    img.getFileName(),
                    img.getFilePath(),
                    img.getContentType(),
                    img.getFileSize()
                ))
                .toList();

            return new PostDTO(
                post.getIdPost(),
                post.getCaption(),
                post.getDescription(),
                post.getLatitude(),
                post.getLongitude(),
                post.getVisitDate().toString(),
                images
            );
        }).toList();
    }

    @Transactional
    public Post createPost(
        String caption,
        String description,
        double latitude,
        double longitude,
        LocalDate visitDate,
        List<MultipartFile> images,
        String username
    ) throws IOException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setUser(user);
        post.setCaption(caption);
        post.setDescription(description);
        post.setLatitude(latitude);
        post.setLongitude(longitude);
        post.setVisitDate(visitDate);

        post = postRepository.save(post);

        if (images != null && !images.isEmpty()) {
            String uploadsDir = "uploads/posts/" + post.getIdPost() + "/";
            Files.createDirectories(Paths.get(uploadsDir));

            for (MultipartFile file : images) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(uploadsDir, fileName);
                file.transferTo(filePath.toFile());

                PostImage postImage = new PostImage();
                postImage.setFileName(file.getOriginalFilename());
                postImage.setFilePath("posts/" + post.getIdPost() + "/" + fileName);
                postImage.setContentType(file.getContentType());
                postImage.setFileSize(file.getSize());
                postImage.setPost(post);

                post.getImages().add(postImage);
            }

            post = postRepository.save(post);
        }

        return post;
    }

    @Transactional
    public void deletePost(Long idPost, String username) {
        postRepository.findById(idPost)
            .ifPresentOrElse(foundPost -> {
                if (!foundPost.getUser().getUsername().equals(username)) {
                    throw new RuntimeException("Unauthorized to delete this post");
                }
                postRepository.delete(foundPost);
            },
            () -> {
                throw new RuntimeException("Post not found");
            });
    }

    public List<PostDTO> getPostsByUsernameDTO(String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));

    return postRepository.findByUser(user).stream().map(post -> {
        List<PostImageDTO> images = post.getImages().stream()
            .map(img -> new PostImageDTO(
           img.getId(),
           img.getFileName(),
           img.getFilePath(),
           img.getContentType(),
           img.getFileSize()
        )).toList();

        return new PostDTO(
            post.getIdPost(),
            post.getCaption(),
            post.getDescription(),
            post.getLatitude(),
            post.getLongitude(),
            post.getVisitDate().toString(),
            images
        );
    }).toList();
}



}