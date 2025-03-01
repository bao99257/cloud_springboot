package com.example.web_security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.web_security.Repo.UsersRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private UsersRepository userRepository;

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public UserDetailsService userDetailsService() {
                return username -> userRepository.findByUsername(username)
                                .map(user -> org.springframework.security.core.userdetails.User
                                                .withUsername(user.getUsername())
                                                .password(user.getPassword())
                                                .roles(user.getRole().replace("ROLE_", ""))
                                                .build())
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/user/**").hasRole("USER")
                                                .requestMatchers("/login", "/register", "/h2-console/**").permitAll()
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/home", true)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout") // Endpoint cụ thể cho đăng xuất
                                                .logoutSuccessUrl("/login") // Chuyển hướng sau khi đăng xuất
                                                .permitAll() // Cho phép tất cả truy cập endpoint logout
                                                .invalidateHttpSession(true) // Xóa session khi đăng xuất
                                                .deleteCookies("JSESSIONID") // Xóa cookie session
                                )
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/h2-console/**"))
                                .headers(headers -> headers
                                                .frameOptions(frameOptions -> frameOptions.sameOrigin()));

                return http.build();
        }
}