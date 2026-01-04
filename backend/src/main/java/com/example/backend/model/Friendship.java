package com.example.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@IdClass(FriendshipId.class)
@Table(name = "friendships")
public class Friendship {
    @Id
    @Column(name = "first_user_id")
    private Long firstUserId;

    @Id
    @Column(name = "second_user_id")
    private Long secondUserId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime creationDate;

    @PrePersist
    protected void onCreate() {
        if (this.creationDate == null) this.creationDate = LocalDateTime.now();
    }

    public Friendship(Long id1, Long id2) {
        if (id1 < id2) {
            this.firstUserId = id1;
            this.secondUserId = id2;
        } else {
            this.firstUserId = id2;
            this.secondUserId = id1;
        }
    }
}
