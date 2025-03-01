// package com.example.web_security.Service;


// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;

// import com.example.web_security.Repo.UserRepository;
// import com.example.web_security.model.User;

// import org.springframework.security.core.authority.SimpleGrantedAuthority;

// import java.util.stream.Collectors;

// @Service
// public class CustomUserDetailsService implements UserDetailsService {

//     @Autowired
//     private UserRepository userRepository;

//     @Override
//     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//         User user = userRepository.findByUsername(username)
//             .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng: " + username));
//         return new org.springframework.security.core.userdetails.User(
//             user.getUsername(),
//             user.getPassword(),
//             user.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
//         );
//     }
// }