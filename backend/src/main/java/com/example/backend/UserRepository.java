package com.example.backend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // dodane pozniej metody np. findByEmail
    //na razie z interfejsu bazowego: findById(), findAll(), save()
}
