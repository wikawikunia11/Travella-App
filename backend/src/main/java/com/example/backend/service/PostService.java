package com.example.backend.service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.model.Post;
import com.example.backend.model.PostImage;
import com.example.backend.model.User;
import com.example.backend.repository.PostImageRepository;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;

import jakarta.transaction.Transactional;


@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostImageRepository postImageRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public List<String> getPostImages(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        List<PostImage> images = postImageRepository.findByPost_IdPost(postId);

        return images.stream()
                .map(img -> "/api/posts/" + postId + "/images/" + img.getFileName())
                .toList();
    }

    public Resource getImageFile(Long postId, String fileName) throws IOException {
        PostImage image = postImageRepository.findByPost_IdPostAndFileName(postId, fileName)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        Path imagePath = Paths.get("/app/uploads/posts", postId.toString(), image.getFilePath());

        if (!Files.exists(imagePath)) {
            throw new RuntimeException("File not found");
        }

        return new UrlResource(imagePath.toUri());
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
            Path postDir = Paths.get("/app/uploads/posts", post.getIdPost().toString());
            Files.createDirectories(postDir);

            System.out.println("Files to upload:");
            for (MultipartFile file : images) {
                System.out.println("- " + file.getOriginalFilename());
            }
            for (MultipartFile file : images) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = postDir.resolve(fileName);
                file.transferTo(filePath.toFile());

                PostImage postImage = new PostImage();
                postImage.setFileName(file.getOriginalFilename());
                postImage.setFilePath(fileName);
                postImage.setPost(post);
                postImageRepository.save(postImage);
            }
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
                deleteImagesForPost(idPost);
                postRepository.delete(foundPost);
            },
            () -> {
                throw new RuntimeException("Post not found");
            });
    }

    private void deleteImagesForPost(Long postId) {
        List<PostImage> images = postImageRepository.findByPost_IdPost(postId);

        for (PostImage image : images) {
            Path imagePath = Paths.get("/app/uploads/posts", String.valueOf(postId), image.getFilePath());
            try {
                Files.deleteIfExists(imagePath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete image file: " + image.getFileName(), e);
            }

            postImageRepository.deleteById(image.getIdImage());
        }

        Path postDir = Paths.get("/app/uploads/posts", String.valueOf(postId));
        try {
            Files.deleteIfExists(postDir);
        } catch (IOException e) {
            System.err.println("Failed to delete post directory: " + postDir);
        }
    }

    public List<Post> getPostsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return postRepository.findByUser(user);
    }
}