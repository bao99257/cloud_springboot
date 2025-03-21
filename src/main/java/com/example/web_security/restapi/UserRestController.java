package com.example.web_security.restapi;


import com.example.web_security.Repo.UsersRepository;
import com.example.web_security.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.util.List;

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
        user.setRole("ROLE_USER");
        usersRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/generateToken")
    public ResponseEntity<String> generateToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            return ResponseEntity.ok(jwtService.generateToken(authRequest.getUsername()));
        } else {
            throw new UsernameNotFoundException("Invalid credentials");
        }
    }

    @GetMapping("/user/profile")
    public ResponseEntity<Users> getUserProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }
        Users user = usersRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/user/update")
    public ResponseEntity<String> updateUserProfile(Authentication authentication, @RequestBody Users updatedUser) {
        Users user = usersRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setName(updatedUser.getName());
        user.setAge(updatedUser.getAge());
        user.setAddress(updatedUser.getAddress());
        usersRepository.save(user);
        return ResponseEntity.ok("Profile updated successfully");
    }

    @GetMapping("/admin/users")
    public ResponseEntity<List<Users>> getAllUsers() {
        return ResponseEntity.ok(usersRepository.findAll());
    }

    @PutMapping("/admin/users/{id}")
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

    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        usersRepository.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    static class AuthRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
















// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.web.bind.annotation.*;

// import com.example.web_security.Repo.UsersRepository;
// import com.example.web_security.model.Users;

// import jakarta.annotation.PostConstruct;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Set;

// @RestController
// @RequestMapping("/api")
// public class UserRestController {

//     @Autowired
//     private UsersRepository userRepository;

//     @Autowired
//     private PasswordEncoder passwordEncoder;

//     @Autowired
//     private JwtService jwtService;

//     // Khởi tạo tài khoản admin mặc định
//     @PostConstruct
//     public void initAdmin() {
//         if (userRepository.findByUsername("admin").isEmpty()) {
//             Users admin = new Users("admin", passwordEncoder.encode("admin123"),
//                     "ROLE_ADMIN", "Admin", 20, "Da Nang");
//             userRepository.save(admin);
//         }
//     }

//     // Trang welcome không cần xác thực
//     @GetMapping("/welcome")
//     public ResponseEntity<String> welcome() {
//         return ResponseEntity.ok("Welcome to the API, this endpoint is not secure!");
//     }

//     // Đăng ký người dùng mới (chỉ tạo USER)
//     @PostMapping("/register")
//     public ResponseEntity<String> registerUser(@RequestBody Users user) {
//         user.setRole("ROLE_USER");
//         user.setPassword(passwordEncoder.encode(user.getPassword()));
//         userRepository.save(user);
//         return ResponseEntity.ok("User registered successfully");
//     }

//     // Lấy thông tin cá nhân của người dùng hiện tại (ROLE_USER hoặc ROLE_ADMIN)
//     @GetMapping("/user/profile")
//     @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
//     public ResponseEntity<Users> getUserProfile(Authentication authentication) {
//         Users user = userRepository.findByUsername(authentication.getName())
//                 .orElseThrow(() -> new IllegalArgumentException("User not found"));
//         return ResponseEntity.ok(user);
//     }

//     @PostMapping("/generateToken")
//     public ResponseEntity<String> generateToken(@RequestBody AuthRequest authRequest) {
//         Authentication authentication = authenticationManager.authenticate(
//                 new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
//         if (authentication.isAuthenticated()) {
//             return ResponseEntity.ok(jwtService.generateToken(authRequest.getUsername()));
//         } else {
//             throw new UsernameNotFoundException("Invalid credentials");
//         }
//     }

//     // Cập nhật thông tin cá nhân của người dùng hiện tại (ROLE_USER hoặc
//     // ROLE_ADMIN)
//     @PutMapping("/user/update")
//     @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
//     public ResponseEntity<String> updateUserProfile(Authentication authentication, @RequestBody Users updatedUser) {
//         Users currentUser = userRepository.findByUsername(authentication.getName())
//                 .orElseThrow(() -> new IllegalArgumentException("User not found"));
//         currentUser.setName(updatedUser.getName());
//         currentUser.setAge(updatedUser.getAge());
//         currentUser.setAddress(updatedUser.getAddress());
//         userRepository.save(currentUser);
//         return ResponseEntity.ok("Profile updated successfully");
//     }

//     // Lấy danh sách tất cả người dùng (chỉ ROLE_ADMIN)
//     @GetMapping("/admin/users")
//     @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//     public ResponseEntity<List<Users>> getAllUsers() {
//         return ResponseEntity.ok(userRepository.findAll());
//     }

//     // Cập nhật thông tin người dùng bởi admin (chỉ ROLE_ADMIN)
//     @PutMapping("/admin/users/{id}")
//     @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//     public ResponseEntity<String> updateUserByAdmin(@PathVariable Long id, @RequestBody Users updatedUser) {
//         Users existingUser = userRepository.findById(id)
//                 .orElseThrow(() -> new IllegalArgumentException("User not found"));
//         existingUser.setName(updatedUser.getName());
//         existingUser.setAge(updatedUser.getAge());
//         existingUser.setAddress(updatedUser.getAddress());
//         if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
//             existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
//         }
//         userRepository.save(existingUser);
//         return ResponseEntity.ok("User updated successfully by admin");
//     }

//     // Xóa người dùng (chỉ ROLE_ADMIN)
//     @DeleteMapping("/admin/users/{id}")
//     @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//     public ResponseEntity<String> deleteUser(@PathVariable Long id) {
//         userRepository.deleteById(id);
//         return ResponseEntity.ok("User deleted successfully");
//     }

//     static class AuthRequest {
//         private String username;
//         private String password;

//         public String getUsername() { return username; }
//         public void setUsername(String username) { this.username = username; }
//         public String getPassword() { return password; }
//         public void setPassword(String password) { this.password = password; }
//     }
// }