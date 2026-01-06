package com.example.backend.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.example.backend.model.Post;
import com.example.backend.model.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser(User user);
    List<Post> findByUserIn(List<User> userList);
    void deleteByIdPost(Long idPost);
}
