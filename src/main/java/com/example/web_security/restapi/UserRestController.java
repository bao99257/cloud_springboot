package com.example.web_security.restapi;

import com.example.web_security.Repo.UsersRepository;
import com.example.web_security.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserRestController {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostConstruct
    public void initAdmin() {
        // Kiểm tra xem có admin chưa, nếu chưa thì tạo mới
        if (usersRepository.findByUsername("admin").isEmpty()) {
            Users admin = new Users("admin", passwordEncoder.encode("admin123"),
                    "ROLE_ADMIN", "Admin", 20, "Da Nang");
            usersRepository.save(admin);
        }
    }

    @GetMapping("/welcome")
    public ResponseEntity<String> welcome() {
        return ResponseEntity.ok("Welcome to the API!");
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER"); // Set default role to USER
        usersRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/generateToken")
    public ResponseEntity<String> generateToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        if (authentication.isAuthenticated()) {
            Users user = usersRepository.findByUsername(authRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Thêm claims với role
            Map<String, Object> claims = new HashMap<>();
            claims.put("roles", user.getRole()); // Thêm dòng này

            return ResponseEntity.ok(jwtService.generateToken(authRequest.getUsername(), claims));
        } else {
            throw new UsernameNotFoundException("Invalid credentials");
        }
    }

    @GetMapping("/user/home")
    public ResponseEntity<Map<String, String>> home(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User is not authenticated");
            return ResponseEntity.status(401).body(errorResponse); // 401 Unauthorized
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to the Home Page!");
        response.put("user", authentication.getName());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/home/profile")
    public ResponseEntity<Users> getUserProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }
        Users user = usersRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/user/home/update")
    public ResponseEntity<String> updateUserProfile(Authentication authentication, @RequestBody Users updatedUser) {
        Users user = usersRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setName(updatedUser.getName());
        user.setAge(updatedUser.getAge());
        user.setAddress(updatedUser.getAddress());
        usersRepository.save(user);
        return ResponseEntity.ok("Profile updated successfully");
    }

    @GetMapping("/admin/home")
    public ResponseEntity<String> adminHome(Authentication authentication) {
        // Kiểm tra nếu người dùng có quyền admin
        if (authentication == null || !AuthorityUtils.authorityListToSet(authentication.getAuthorities())
                .contains("ROLE_ADMIN")) {
            return ResponseEntity.status(403).body("Access Denied: You do not have admin privileges.");
        }

        // Nếu người dùng là admin, trả về trang chủ cho admin
        return ResponseEntity.ok("Welcome to the Admin Home Page!");
    }

    @GetMapping("/admin/home/users")
    public ResponseEntity<List<Users>> getAllUsers() {
        return ResponseEntity.ok(usersRepository.findAll());
    }

    @PutMapping("/admin/home/users/{id}")
    public ResponseEntity<String> updateUserByAdmin(@PathVariable Long id, @RequestBody Users updatedUser) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setUsername(updatedUser.getUsername());
        user.setName(updatedUser.getName());
        user.setAge(updatedUser.getAge());
        user.setAddress(updatedUser.getAddress());
        user.setRole(updatedUser.getRole());
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        usersRepository.save(user);
        return ResponseEntity.ok("User updated by admin");
    }

    @DeleteMapping("/admin/home/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        usersRepository.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PostMapping("/admin/home/users/create")
    public ResponseEntity<String> createUserByAdmin(@RequestBody Users user) {
        // Kiểm tra xem username đã tồn tại chưa
        if (usersRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        // Mã hóa mật khẩu
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Đảm bảo role có prefix ROLE_
        String role = user.getRole();
        if (role == null || role.isBlank()) {
            user.setRole("ROLE_USER");
        } else {
            user.setRole(role.startsWith("ROLE_") ? role : "ROLE_" + role);
        }

        usersRepository.save(user);
        return ResponseEntity.ok("User created successfully");
    }

    @PutMapping("/admin/home/users/{id}/assign-role")
    public ResponseEntity<String> assignRoleToUser(@PathVariable Long id, @RequestBody String role) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Đảm bảo role có prefix ROLE_
        String formattedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        user.setRole(formattedRole);

        usersRepository.save(user);
        return ResponseEntity.ok("Role assigned successfully");
    }

    static class AuthRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
