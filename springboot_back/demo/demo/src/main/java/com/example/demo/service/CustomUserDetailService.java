package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    /*
        UserDetailsService : 스프링 시큐리티(Spring Security)에서 사용자의 인증 정보(아이디, 비밀번호, 권한 등)를
                                                          데이터베이스나 외부 저장소에서 불러오는 핵심 인터페이스
        사용자 상세 정보(UserDetails)를 조회하는 역할
     */

    public final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    } // end of 생성자

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // DB에서 username으로 사용자 조회
        // findByUsername()의 반환 타입이 Optional<User> 이므로
        // 값이 있으면 User를 꺼내고, 없으면 예외를 발생시킴
        User user = userRepository.findByUsername(username)
                                .orElseThrow( ()-> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        // 조회한 User 엔티티를 스프링 시큐리티가 이해할 수 있는
        // UserDetails 객체(CustomUserDetails)로 감싸서 반환
        return new CustomUserDetails(user);
    } // end of loadUserByUsername
}
