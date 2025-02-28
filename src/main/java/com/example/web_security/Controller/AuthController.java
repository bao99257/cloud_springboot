package com.example.web_security.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.web_security.Repo.UserRepository;
import com.example.web_security.model.User;

import java.util.Set;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login"; // Trả về file login.html
    }

    @GetMapping("/register")
    public String register() {
        return "register"; // Trả về file register.html
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // Mã hóa mật khẩu
        user.setRoles(Set.of("USER")); // Gán vai trò USER
        userRepository.save(user); // Lưu vào database
        return "redirect:/login"; // Chuyển hướng về trang login
    }

    @GetMapping("/home")
    public String home() {
        return "home"; // Trả về file home.html
    }
}