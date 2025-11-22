package com.plyy.plyyReboot.client.oauth;

import com.plyy.plyyReboot.client.oauth.exception.EmailInvalidException;
import com.plyy.plyyReboot.client.oauth.exception.InvalidRoleException;
import com.plyy.plyyReboot.client.oauth.exception.MissingAttributeException;
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
            throw new EmailInvalidException("이메일은 필수 값입니다.");
        }
        if (role == null || role.isBlank()) {
            throw new InvalidRoleException("Role은 필수 값입니다.");
        }
        if (attributes == null) {
            throw new MissingAttributeException("속성(Attributes)은 필수 값입니다.");
        }

        this.name = email;
        this.authorities = List.of(new SimpleGrantedAuthority(role));
        this.attributes = Map.copyOf(attributes);
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