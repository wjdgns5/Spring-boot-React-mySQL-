package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Optional<> : null을 직접 다루지 않고 Optional 객체로 감싸 안전하게 처리
    Optional<User> findByUsername(String username);
}
