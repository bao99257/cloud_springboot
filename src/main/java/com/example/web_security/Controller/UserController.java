package com.example.web_security.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.web_security.Repo.UsersRepository;
import com.example.web_security.Repo.ProductRepository;
import com.example.web_security.model.Users;
import com.example.web_security.model.Product;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Path;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Controller
public class UserController {

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ Tạo admin mặc định
    @PostConstruct
    public void initAdmin() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            Users admin = new Users("admin", passwordEncoder.encode("admin123"),
                    "ROLE_ADMIN", "Admin", 20, "Da Nang");
            userRepository.save(admin);
        }
    }

    // ------------------- LOGIN & REGISTER -------------------
    @GetMapping("/login")
    public String login(@RequestParam(value = "logout", required = false) String logout, Model model) {
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new Users());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute Users user) {
        user.setRole("ROLE_USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/login";
    }

    // ------------------- ADMIN QUẢN LÝ USER -------------------
    @GetMapping("/admin")
    public String adminPage(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("products", productRepository.findAll()); // ✅ thêm danh sách sản phẩm
        return "admin";
    }

    @GetMapping("/admin/create")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new Users());
        return "create_user";
    }

    @PostMapping("/admin/create")
    public String createUser(@ModelAttribute Users user) {
        user.setRole("ROLE_USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/admin";
    }

    @GetMapping("/admin/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        model.addAttribute("user", user);
        return "edit";
    }

    @PostMapping("/admin/update")
    public String updateUser(@ModelAttribute Users user) {
        Users existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        existingUser.setUsername(user.getUsername());
        existingUser.setName(user.getName());
        existingUser.setAge(user.getAge());
        existingUser.setAddress(user.getAddress());
        existingUser.setRole(user.getRole());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(existingUser);
        return "redirect:/admin";
    }

    @GetMapping("/admin/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/editRole/{id}")
    public String editUserRoleForm(@PathVariable Long id, Model model) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        model.addAttribute("user", user);
        return "edit_role";
    }

    @PostMapping("/admin/updateRole")
    public String updateUserRole(@RequestParam Long id, @RequestParam String role) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRole(role);
        userRepository.save(user);
        return "redirect:/admin";
    }

    // ------------------- USER TRANG CÁ NHÂN -------------------
    @GetMapping("/user")
    public String userPage(Authentication authentication, Model model) {
        Users user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        model.addAttribute("user", user);
        return "user";
    }

    @GetMapping("/user/edit")
    public String editUserPage(Authentication authentication, Model model) {
        Users user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        model.addAttribute("user", user);
        return "user_edit";
    }

    @PostMapping("/user/update")
    public String updatePersonalInfo(Authentication authentication, @ModelAttribute Users user) {
        Users currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        currentUser.setName(user.getName());
        currentUser.setAge(user.getAge());
        currentUser.setAddress(user.getAddress());
        userRepository.save(currentUser);
        return "redirect:/user";
    }

    // ------------------- CRUD PRODUCT (Admin) -------------------

    @GetMapping("/admin/products/create")
    public String showCreateProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "create_product";
    }

    @PostMapping("/admin/products/create")
    public String createProduct(@ModelAttribute Product product) {

        // KHÔNG upload file nữa, chỉ lưu imageUrl do user nhập
        productRepository.save(product);

        return "redirect:/admin";
    }

    @GetMapping("/admin/products/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        model.addAttribute("product", product);
        return "edit_product";
    }

    @PostMapping("/admin/products/update")
    public String updateProduct(@ModelAttribute Product product) {

        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setImageUrl(product.getImageUrl()); // giữ URL

        productRepository.save(existingProduct);

        return "redirect:/admin";
    }

    @GetMapping("/admin/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return "redirect:/admin";
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam("keyword") String keyword, Model model) {
        List<Product> results = productRepository.findByNameContainingIgnoreCase(keyword);
        model.addAttribute("products", results);
        model.addAttribute("keyword", keyword);
        return "search";
    }

    // ------------------- SHOP (User xem sản phẩm) -------------------
    @GetMapping("/shop")
    public String shopPage(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "shop";
    }
}
