package com.example.demo.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

// 스프링 컨테이너가 자동으로 탐색(컴포넌트 스캔)하여 빈(Bean)으로 등록하게 하는 어노테이션
@Component
public class JwtTokenProvider {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long expiration = 1000L * 60 * 60; // 1시간 , expiration : "만료"

    // JWT 토큰 생성 과정
    public String generateToken(String username) {
        return Jwts.builder() // JWT 생성 시작
                .setSubject(username) // 토큰의 주제(subject) 설정 -> 여기서는 사용자 아이디(username)를 넣음
                .setIssuedAt(new Date()) // Issued : 발급하다. // 토큰 발급 시간 설정
                .setExpiration(new Date(System.currentTimeMillis()+expiration)) // 토큰 만료 시간 지정
                .signWith(key) // 비밀키(key)로 서명 -> 토큰 위변조 방지
                .compact(); // 최종적으로 JWT 문자열 형태로 압축해서 반환
    } // end of generateToken

    // 엑세스 토큰
    public String generateAccessToken(String username, String role) {
        long expiration_30m = 1000L * 60 * 30; // (60초 * 30분) 30분

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() +expiration_30m) )
                .signWith(key)
                .compact();
    } // end of generateAccessToken

    // 리프레시 토큰
    public String generateRefreshToken(String username) {
        long expiration_7d = 1000L * 60 * 60 * 24 * 7; // (60초 * 60분) * 24 = 1일 , 1일 * 7 = 7일

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+ expiration_7d) )
                .signWith(key)
                .compact();
    } // end of getUsernameFromToken


    // JWT 토큰 내용 확인 과정
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder() // JWT 파서 생성 시작 (parser : 분해)
                   .setSigningKey(key) // // 토큰 검증에 사용할 서명 키 설정
                   .build() // parser 객체 생성 완료
                   .parseClaimsJws(token) // 전달받은 JWT 토큰을 파싱해서 내부 Claims(내용) 추출
                   .getBody() // 토큰의 payload 부분 가져오기 (내용물 가져오기)
                   .getSubject(); // payload 안에 저장된 subject 값(username) 반환
    } // end of getUsernameFromToken

    // JWT 토큰 시간 유효 또는 우리가 발급한 JSON 토큰이 맞는지 확인
    public boolean validateToken(String token) {
        try{
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    } // end of validateToken
    

}
