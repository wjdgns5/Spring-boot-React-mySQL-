package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    public final UserService userService;
    public final UserRepository userRepository;

    public AdminController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    } // end of 생성자

    // http://localhost:8080/api/admin/dashboard
    @GetMapping("/dashboard")
    public String adminOnly() {
        return "관리자 전용 대시보드입니다.";
    }

    // http://localhost:8080/api/admin/user
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }





}
