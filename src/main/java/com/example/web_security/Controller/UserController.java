package com.example.web_security.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.web_security.Repo.UsersRepository;
import com.example.web_security.Repo.ProductRepository;
import com.example.web_security.model.Users;
import com.example.web_security.model.Product;

import jakarta.annotation.PostConstruct;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ------------------- INIT ADMIN -------------------
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

    // ------------------- ADMIN PAGE -------------------
    @GetMapping("/admin")
    public String adminPage(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("products", productRepository.findAll());
        return "admin";
    }

    // ------------------- MANAGE USER -------------------
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

    // ------------------- USER PROFILE -------------------
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

    // -------------------------------------------------------------
    // ---------------------- PRODUCT CRUD (URL ONLY) ---------------
    // -------------------------------------------------------------

    // CREATE PRODUCT
    @GetMapping("/admin/products/create")
    public String showCreateProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "create_product";
    }

    @PostMapping("/admin/products/create")
    public String createProduct(
            @RequestParam("name") String name,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam("imageUrl") String imageUrl) {

        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setDescription(description);
        p.setImageUrl(imageUrl);

        productRepository.save(p);

        return "redirect:/admin";
    }

    // EDIT PRODUCT
    @GetMapping("/admin/products/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        model.addAttribute("product", product);
        return "edit_product";
    }

    @PostMapping("/admin/products/update")
    public String updateProduct(
            @RequestParam("id") Long id,
            @RequestParam("name") String name,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam("imageUrl") String imageUrl) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        product.setName(name);
        product.setPrice(price);
        product.setDescription(description);
        product.setImageUrl(imageUrl);

        productRepository.save(product);
        return "redirect:/admin";
    }

    // DELETE PRODUCT
    @GetMapping("/admin/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return "redirect:/admin";
    }

    // SEARCH
    @GetMapping("/search")
    public String searchProducts(@RequestParam("keyword") String keyword, Model model) {
        List<Product> results = productRepository.findByNameContainingIgnoreCase(keyword);
        model.addAttribute("products", results);
        model.addAttribute("keyword", keyword);
        return "search";
    }

    // SHOP PAGE
    @GetMapping("/shop")
    public String shopPage(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "shop";
    }

    // PRODUCT DETAIL PAGE
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        model.addAttribute("product", p);
        return "product_detail";
    }

}
