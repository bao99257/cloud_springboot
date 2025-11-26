package com.example.web_security.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.web_security.Repo.UsersRepository;
import com.example.web_security.restapi.JwtAuthFilter;

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
        public AuthenticationManager authenticationManager(HttpSecurity http,
                        UserDetailsService userDetailsService,
                        PasswordEncoder passwordEncoder) throws Exception {

                AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
                auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
                return auth.build();
        }

        // ================== CORS CHO API ==================
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration config = new CorsConfiguration();

                config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
                config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
                config.setAllowCredentials(true);
                config.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);
                return source;
        }

        // ================== SECURITY API ==================
        @Bean
        @Order(1)
        public SecurityFilterChain apiFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {

                http.securityMatcher("/api/**")
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())
                                .formLogin(form -> form.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/register", "/api/generateToken", "/api/welcome")
                                                .permitAll()
                                                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        // ================== SECURITY WEB FORM LOGIN ==================
        @Bean
        @Order(2)
        public SecurityFilterChain formLoginFilterChain(HttpSecurity http) throws Exception {

                http.authorizeHttpRequests(auth -> auth
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/user/**").hasRole("USER")

                                // Cho phép login/register
                                .requestMatchers("/login", "/register", "/h2-console/**").permitAll()

                                // Ảnh, uploads
                                .requestMatchers("/images/**", "/uploads/**").permitAll()

                                // ⭐ Cho phép giỏ hàng (GET, POST, DELETE)
                                .requestMatchers("/cart/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/cart/add/**").permitAll()

                                // Trang shop/home/search + product detail
                                .requestMatchers("/", "/shop", "/search", "/product/**").permitAll()

                                .anyRequest().authenticated())

                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .successHandler((request, response, authentication) -> {
                                                        var authorities = authentication.getAuthorities();
                                                        if (authorities.stream()
                                                                        .anyMatch(a -> a.getAuthority()
                                                                                        .equals("ROLE_ADMIN"))) {
                                                                response.sendRedirect("/admin");
                                                        } else {
                                                                response.sendRedirect("/shop");
                                                        }
                                                })
                                                .permitAll())

                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login")
                                                .permitAll()
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID"))

                                // ⭐ FIX CSRF — BỎ CHẶN CHO GIỎ HÀNG
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers(
                                                                "/h2-console/**",
                                                                "/cart/**" // <-- QUAN TRỌNG
                                                ))

                                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

                return http.build();
        }
}
