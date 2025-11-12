package com.plyy.plyyReboot.client.oauth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class PlyyOAuth2Principal implements OAuth2User {
    private final String name; // 이메일
    private final List<GrantedAuthority> authorities; // 권한 목록
    private final Map<String, Object> attributes; // 필터링된 사용자 정보

   public PlyyOAuth2Principal(String email, String role, Map<String, Object> attributes) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 null 또는 빈 값일 수 없습니다.");
        }
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("역할(Role)은 null 또는 빈 값일 수 없습니다.");
        }
        if (attributes == null) {
            throw new IllegalArgumentException("속성(Attributes)은 null일 수 없습니다.");
        }

        this.name = email;
        this.authorities = List.of(new SimpleGrantedAuthority(role));
        this.attributes = Map.copyOf(attributes); // 방어적 복사
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return name;
    }
}