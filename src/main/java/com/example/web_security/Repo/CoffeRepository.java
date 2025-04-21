package com.example.web_security.Repo;

import com.example.web_security.model.Coffee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoffeRepository extends JpaRepository<Coffee, Long> {
    List<Coffee> findByStatus(String status);

    boolean existsByNumber(String number);
}
