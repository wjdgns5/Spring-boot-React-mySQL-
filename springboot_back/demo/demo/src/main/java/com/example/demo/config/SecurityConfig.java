package com.example.demo.config;

import com.example.demo.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception{

        http
                // AbstractHttpConfigurer : JWT 기반 인증 환경에서 기본 인증 방식을 끄는 데 사용
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable) // 기본적으로 제공하는 로그인 폼 비활성화
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth-> auth
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/admin/**").hasAnyRole("ADMIN") // ADMIN 만 접근가능
                        .requestMatchers("/api/private/**").hasAnyRole("USER","ADMIN")
                            // hasAnyRole : 사용자가 지정된 권한(Role) 중 하나라도 가지고 있으면 접근을 허용하는 메소드
                        .anyRequest().denyAll())

                // 필터 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Cors 해결하기
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // CORS 설정 정보를 담는 객체 생성
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 출처(Origin) 설정
        // React 개발 서버 주소가 보통 http://localhost:5173 이라서 이 주소를 허용
        // 주의: 끝에 "/" 없이 쓰는 경우가 더 일반적임
        config.addAllowedOrigin("http://localhost:5173");

        // 모든 요청 헤더 허용
        // 예: Authorization, Content-Type 등
        config.addAllowedHeader("*");

        // 모든 HTTP 메서드 허용
        // 예: GET, POST, PUT, DELETE 등
        config.addAllowedMethod("*");

        // 쿠키, 세션, Authorization 헤더 같은 인증 정보를 함께 보낼 수 있도록 허용
        // true면 프론트에서도 withCredentials: true 설정이 필요할 수 있음
        config.setAllowCredentials(true);

        // URL 패턴별로 CORS 설정을 등록하기 위한 객체 생성
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // 모든 URL("/**") 요청에 대해 위의 CORS 설정 적용
        source.registerCorsConfiguration("/**", config);

        // 설정된 CORS 정보 반환
        return source;
    } // end of corsConfigurationSource


}
