package com.example.demo.controller;

import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.LoginResponseDTO;
import com.example.demo.dto.UserRequestDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    public final UserService userService;
    public final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    } // end of 생성자
    
    // 회원가입 http://localhost:8080/api/auth/signup
    @PostMapping("/signup")
    public String signUp(@RequestBody UserRequestDTO userRequestDTO) {
        userService.signUp(userRequestDTO);
        return "회원가입 성공";
    } // end of signUp

    // 로그인 http://localhost:8080/api/auth/login
    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO){
        return userService.login(loginRequestDTO);
       // return "로그인 성공";
    } // end of login

    // 토큰 만료 시 refreshToken 재 발급 http://localhost:8080/api/auth/refresh
    @PostMapping("/refresh")
    public LoginRequestDTO refresh(@RequestHeader("Authorization") String refreshToken) {
        // @RequestHeader : HTTP 요청 헤더(Header)의 특정 값을 컨트롤러 메서드의 파라미터로 바인딩(가져오기)하는 어노테이션

        // 헤더에 있는 refreshToken을 가져와야 한다.
        String token = refreshToken.replace("Bearer ", "");

        // 토큰이 아직 유효한지 확인하는 과정
        if(!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("서버에 저장된 Refresh Token이 유효하지 않습니다.");
        }

        // JWT 토큰 내용 확인 과정
        String username = jwtTokenProvider.getUsernameFromToken(token);

        // 토큰을 뜯었고 user는 있는데 db에 존재하지 않을 수도 있다.
        User user = userRepository.findByUsername(username)
                    .orElseThrow( ()-> new RuntimeException("사용자가 존재하지 않습니다."));
        
        // 내가 가지고 있는 RefreshToken과 DB에 있는 RefreshToken이 같은지 다른지 비교
        if(!token.equals(user.getRefreshToken())) {
            throw  new RuntimeException("서버에 저장된 Refresh Token과 일치하지 않습니다.");
        }

        // Access Token과 Refresh Token을 생성한다.
        String newAccessToken = jwtTokenProvider.generateAccessToken(username);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);

        user.setRefreshToken(newAccessToken);
        userRepository.save(user);

        return ResponseEntity.ok(new LoginRequestDTO(newAccessToken, newRefreshToken)).getBody();
    } // end of refresh

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String accessToken) {
        String token = accessToken.replace("Bearer ", "");
        String username = jwtTokenProvider.getUsernameFromToken(token); // JWT 토큰 내용 확인 과정
        
        // 사용자 유무 확인
        User user = userRepository.findByUsername(username)
                    .orElseThrow( ()-> new RuntimeException("사용자가 존재하지 않습니다."));

        // 로그아웃 할 때 RefreshToken을 Null로 하여 초기화 한다.
        user.setRefreshToken(null);
        // DB에 RefreshToken 이력을 저장 한다.
        userRepository.save(user);

        return "로그아웃 성공";
    } // end of logout
    
}
