package com.ks.taskmanager.controller;

import com.ks.taskmanager.entity.User;
import com.ks.taskmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

    // Get all users
    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    // Get info of the current user
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(){
        return userService.getCurrentUser();
    }

    // Create user
    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody User user) {
        return userService.signup(user);
    }

    // Get the token for Auth
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User loginRequest){
        return userService.login(loginRequest);
    }
}
