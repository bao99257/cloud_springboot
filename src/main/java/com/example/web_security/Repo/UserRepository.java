package com.example.web_security.Repo;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.web_security.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}