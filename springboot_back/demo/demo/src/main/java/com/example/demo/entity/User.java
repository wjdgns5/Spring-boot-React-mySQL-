package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

// lombok 어노테이션
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "role", nullable = false, length = 10)
    private String role;

    @Column(name = "refresh_token", nullable = true, length = 500)
    private String refreshToken;

}
