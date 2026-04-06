package com.example.demo.controller;

import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.LoginResponseDTO;
import com.example.demo.dto.UserRequestDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
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
//    @PostMapping("/login")
//    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO){
//        return userService.login(loginRequestDTO);
//       // return "로그인 성공";
//    } // end of login

    // 로그인 http://localhost:8080/api/auth/login
    @PostMapping("/login")
    public ResponseEntity<LoginRequestDTO> login(@RequestBody LoginRequestDTO request, HttpServletResponse response) {

        LoginResponseDTO tokens = userService.login(request);

        // 쿠키 생성
        Cookie refreshCookie = new Cookie("refreshToken", tokens.getRefreshToken());
        refreshCookie.setPath("/"); // 모든 경로에서 쿠키 전송 가능
        refreshCookie.setHttpOnly(true); // true로 하면 자바스크립트 접근 불가, 즉,XSS 공격으로 쿠키 탈취되는 위험을 줄이는 용도
        refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        response.addCookie(refreshCookie); // 응답(response)에 쿠키 추가, 브라우저가 Set-Cookie 형태로 받아서 저장함

        // accessToken은 응답 body(JSON)로 내려줌
        // refreshToken은 이미 쿠키로 보냈기 때문에 body에는 null 처리
        return ResponseEntity.ok(new LoginRequestDTO(tokens.getAccessToken(), null));
    } // end of login

    // 토큰 만료 시 refreshToken 재 발급 http://localhost:8080/api/auth/refresh
//    @PostMapping("/refresh")
//    public LoginRequestDTO refresh(@RequestHeader("Authorization") String refreshToken) {
//        // @RequestHeader : HTTP 요청 헤더(Header)의 특정 값을 컨트롤러 메서드의 파라미터로 바인딩(가져오기)하는 어노테이션
//
//        // 헤더에 있는 refreshToken을 가져와야 한다.
//        String token = refreshToken.replace("Bearer ", "");
//
//        // 토큰이 아직 유효한지 확인하는 과정
//        if(!jwtTokenProvider.validateToken(token)) {
//            throw new RuntimeException("서버에 저장된 Refresh Token이 유효하지 않습니다.");
//        }
//
//        // JWT 토큰 내용 확인 과정
//        String username = jwtTokenProvider.getUsernameFromToken(token);
//
//        // 토큰을 뜯었고 user는 있는데 db에 존재하지 않을 수도 있다.
//        User user = userRepository.findByUsername(username)
//                    .orElseThrow( ()-> new RuntimeException("사용자가 존재하지 않습니다."));
//
//        // 내가 가지고 있는 RefreshToken과 DB에 있는 RefreshToken이 같은지 다른지 비교
//        if(!token.equals(user.getRefreshToken())) {
//            throw  new RuntimeException("서버에 저장된 Refresh Token과 일치하지 않습니다.");
//        }
//
//        // Access Token과 Refresh Token을 생성한다.
//        String newAccessToken = jwtTokenProvider.generateAccessToken(username);
//        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);
//
//        user.setRefreshToken(newAccessToken);
//        userRepository.save(user);
//
//        return ResponseEntity.ok(new LoginRequestDTO(newAccessToken, newRefreshToken)).getBody();
//    } // end of refresh

    // 토큰 만료 시 refreshToken를 통해 AccessToken 재 발급 http://localhost:8080/api/auth/refresh
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {

        String refreshToken = null;
        if(request.getCookies() != null) {
            for (Cookie cookie: request.getCookies()) {
                if(cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if(refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 리프레시 토큰 입니다.");
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username).orElseThrow( ()-> new RuntimeException("사용자가 존재하지 않습니다."));

        if(!refreshToken.equals(user.getRefreshToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("서버에 저장된 리프레시 토큰과 다릅니다.");
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(username);

        return ResponseEntity.ok(new LoginResponseDTO(newAccessToken, null));
    } // end of refresh

//    @PostMapping("/logout")
//    public String logout(@RequestHeader("Authorization") String accessToken) {
//        String token = accessToken.replace("Bearer ", "");
//        String username = jwtTokenProvider.getUsernameFromToken(token); // JWT 토큰 내용 확인 과정
//
//        // 사용자 유무 확인
//        User user = userRepository.findByUsername(username)
//                    .orElseThrow( ()-> new RuntimeException("사용자가 존재하지 않습니다."));
//
//        // 로그아웃 할 때 RefreshToken을 Null로 하여 초기화 한다.
//        user.setRefreshToken(null);
//        // DB에 RefreshToken 이력을 저장 한다.
//        userRepository.save(user);
//
//        return "로그아웃 성공";
//    } // end of logout

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String accessToken, HttpServletResponse response) {

        String token = accessToken.replace("Bearer ", "");
        String username = jwtTokenProvider.getUsernameFromToken(token); // JWT 토큰 내용 확인 과정

        // 사용자 유무 확인
        User user = userRepository.findByUsername(username)
                    .orElseThrow( ()-> new RuntimeException("사용자가 존재하지 않습니다."));

        // 로그아웃 할 때 RefreshToken을 Null로 하여 초기화 한다.
        user.setRefreshToken(null);
        // DB에 RefreshToken 이력을 저장 한다.
        userRepository.save(user);

        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok("로그아웃 성공");
    } // end of logout
    
}
