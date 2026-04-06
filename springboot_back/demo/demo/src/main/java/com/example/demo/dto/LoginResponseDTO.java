package com.example.demo.dto;

import lombok.*;

// Lombok 어노테이션
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String accessToken;
    private String refreshToken;
}
