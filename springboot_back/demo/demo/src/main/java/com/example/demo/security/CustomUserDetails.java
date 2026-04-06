package com.example.demo.security;

import com.example.demo.entity.User;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    /*
        UserDetails : 인증(Authentication) 및 인가(Authorization) 처리를 위해 사용자 정보를 규격화하고,
                                                            세션에 안전하게 저장하여 재사용하기 위함
    */

    public final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    } // end of 생성자

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // SimpleGrantedAuthority : 사용자의 권한(Authority) 또는 역할(Role)을 문자열 형태로 표현하고 관리
        return List.of(new SimpleGrantedAuthority(user.getRole()));
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User getUser() {
        return user;
    }
}

