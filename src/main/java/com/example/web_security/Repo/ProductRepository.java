package com.example.web_security.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.web_security.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Có thể bổ sung custom query nếu cần, ví dụ:
    List<Product> findByNameContainingIgnoreCase(String keyword);
    // List<Product> findByNameContaining(String keyword);
}
