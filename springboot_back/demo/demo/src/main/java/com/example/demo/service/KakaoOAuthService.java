package com.example.demo.service;

import com.example.demo.dto.LoginResponseDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class KakaoOAuthService {

    public final JwtTokenProvider jwtTokenProvider;
    public final UserRepository userRepository;
    public final RestTemplate restTemplate = new RestTemplate();

    public KakaoOAuthService(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    } // end of 생성자

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    public LoginResponseDTO kakaoLogin(String code, HttpServletResponse response) {

        // 1. 인가코드로 토큰 요청
        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(params, headers);

        System.out.println("clientId = [" + clientId + "]");
        System.out.println("redirectUri = [" + redirectUri + "]");
        System.out.println("code = [" + code + "]");

        // postForEntity:  HTTP POST 요청을 보내고 그 결과로 ResponseEntity 객체를 반환받는 함수
        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUrl, tokenRequest, Map.class);
        String kakaoAccessToken = (String) tokenResponse.getBody().get("access_token");

        // 2. 사용자 정보 요청
        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.setBearerAuth(kakaoAccessToken);
        HttpEntity<?> userInfoRequest = new HttpEntity<>(userInfoHeaders);

        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                userInfoRequest,
                Map.class
        );

        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfoResponse.getBody().get("kakao_account");
        String email = (String) kakaoAccount.get("email");
        String username = "kakao_" + userInfoResponse.getBody().get("id");

        // 3. 사용자 등록 또는 조회
        User user = userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User newUser = new User(username, null, "ROLE_USER"); // 비밀번호 null로 설정
                    return userRepository.save(newUser);
                });

        // 4. JWT 생성
        String accessToken = jwtTokenProvider.generateAccessToken(username, user.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(username);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // 5. 쿠키 설정
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);
        refreshCookie.setPath("/");
        response.addCookie(refreshCookie);

        return new LoginResponseDTO(accessToken, null);
    }




}
