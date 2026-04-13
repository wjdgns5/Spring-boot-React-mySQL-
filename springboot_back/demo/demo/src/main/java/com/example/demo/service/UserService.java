package com.example.demo.service;

import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.LoginResponseDTO;
import com.example.demo.dto.UserRequestDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public final UserRepository userRepository;
    public final PasswordEncoder passwordEncoder;
    public final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    } // end of 생성자

    // 회원가입
    public void signUp(UserRequestDTO userRequestDTO) {
        // .isPresent(): Optional<> 객체가 값을 가지고 있는지(true) 아니면 비어 있는지(false) 확인
        // .isPresent(): 값이 있으면 → true, 값이 없으면 → false
        if(userRepository.findByUsername(userRequestDTO.getUsername()).isPresent() ) {
            // 사용자가 이미 존재하면 값이 있다는 거고 그 뜻은 true 라는 뜻으로 if()가 작동
            // 이미 존재하는 가입자
            throw new RuntimeException("이미 존재하는 사용자 입니다.");
        }

        User user = User.builder()
                .username(userRequestDTO.getUsername())
                .password(passwordEncoder.encode(userRequestDTO.getPassword()))
                .role("ROLE_USER")
                .build();

        userRepository.save(user);
    } // end of signUp

    // 로그인
    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow( ()-> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 비밀번호가 맞는지 확인
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // ---- 로그인 할때 "Access 토큰"과 "Refresh 토큰"을 함께 보내줘야 한다. ----
        // AccessToken(엑세스 토큰)을 보내야 한다.
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername(), user.getRole());
        // RefreshToken(리프레시 토큰)을 보내야 한다.
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

        // DB에 User Entity의 refresh_token 컬럼에 데이터를 저장하기 위해 set()으로 데이터를 넣는다.
        user.setRefreshToken(refreshToken);

        // DB에 User Entity의 refresh_token에 데이터를 저장한다.
        userRepository.save(user);

        return new LoginResponseDTO(accessToken, refreshToken);

    } // end of login


} // end of UserService
