package com.ks.taskmanager.service;

import com.ks.taskmanager.entity.User;
import com.ks.taskmanager.repository.UserRepository;
import com.ks.taskmanager.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(PasswordEncoder passwordEncoder, JwtUtil jwtUtil){
        this.passwordEncoder=passwordEncoder;
        this.jwtUtil=jwtUtil;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public ResponseEntity<User> getCurrentUser(){
        return ResponseEntity.ok((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    public ResponseEntity<User> createUser(User user){
        return ResponseEntity.ok(userRepository.save(user));
    }

    public ResponseEntity<User> signup(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        return ResponseEntity.ok(userRepository.save(user));
    }

    public ResponseEntity<String> login(User loginRequest){
        User user =userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(()-> new RuntimeException("User not found!"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            return ResponseEntity.status(401).body("Invalid password!");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(token);
    }
}
