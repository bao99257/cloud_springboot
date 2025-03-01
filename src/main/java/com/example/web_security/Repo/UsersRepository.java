package com.example.web_security.Repo;



import org.springframework.data.jpa.repository.JpaRepository;

import com.example.web_security.model.Users;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
}