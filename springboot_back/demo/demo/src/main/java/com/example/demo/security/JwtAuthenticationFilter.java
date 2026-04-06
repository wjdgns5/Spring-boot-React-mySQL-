package com.example.demo.security;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // Custom 필터 등록 - OncePerRequestFilter : JWT 인증, 로깅, 공통 보안 처리 필터 구현 시 자주 사용
    // OncePerRequestFilter : 요청당 한 번만 실행되도록 보장하는 필터

    public final JwtTokenProvider jwtTokenProvider;
    public final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    } // end of 생성자

    // doFilterInternal() : 스프링 시큐리티나 스프링 웹 필터에서, 요청이 들어올 때마다 실행되는 핵심 필터 메서드 
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);

        if(token != null && jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromToken(token); // 토큰 내용 확인
            User user = userRepository.findByUsername(username).orElse(null); // db에서 username 조회

            // user 가 null 이 아닌 경우
            if(user != null) {
                // UsernamePasswordAuthenticationToken : 이 사용자가 인증되었다 는 정보를 담는 상자
                // principal : 누구인지
                // credentials : 비밀번호 같은 인증 정보
                // authorities : 권한 목록
              UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(user, null, null);

              // 스프링 시큐리티에게 "현재 요청은 이 사용자가 인증된 상태다” 라고 등록하는 부분
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } // end of if()

        filterChain.doFilter(request, response);

    } // end of doFilterInternal

    // HTTP 요청 헤더(Header) 등에서 인증 토큰(JWT)을 추출하여 해석하거나 분리해내는 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization"); //Authorization: Bearer abcdefg12345

        if(bearer != null && bearer.startsWith("Bearer ")) { // 값이 "Bearer "로 시작하는지 확인
            return bearer.substring(7); // substr(시작, 길이)는 시작 위치부터 입력한 길이만큼 잘라냄.
            // "bearer " 이거 길이가 7자리라서 "bearer "를 잘라낸다는 뜻?
        }
        return null;
    } // end of resolveToken

}
