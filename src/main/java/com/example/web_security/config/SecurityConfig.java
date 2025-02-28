package com.example.web_security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService; // Đảm bảo đã inject CustomUserDetailsService

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/register", "/login", "/h2-console/**").permitAll() // Cho phép truy cập H2
                                                                                              // Console
                        .requestMatchers("/home").authenticated())
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .defaultSuccessUrl("/home")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout") // Endpoint cụ thể cho đăng xuất
                        .logoutSuccessUrl("/login") // Chuyển hướng sau khi đăng xuất
                        .permitAll() // Cho phép tất cả truy cập endpoint logout
                        .invalidateHttpSession(true) // Xóa session khi đăng xuất
                        .deleteCookies("JSESSIONID") // Xóa cookie session
                )
                // Cấu hình để H2 Console hoạt động đúng
                .headers(headers -> headers
                        .frameOptions().disable() // Vô hiệu hóa X-Frame-Options để H2 Console hiển thị trong iframe
                        .cacheControl().disable() // Tắt cache control nếu cần
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**") // Vô hiệu hóa CSRF cho H2 Console
                // .ignoringRequestMatchers("/logout") // Xóa dòng này nếu muốn bật CSRF cho
                // /logout
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}