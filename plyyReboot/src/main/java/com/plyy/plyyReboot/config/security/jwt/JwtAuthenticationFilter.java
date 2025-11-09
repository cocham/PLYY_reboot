package com.plyy.plyyReboot.config.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Request Header에서 "Authorization" 토큰을 꺼냅니다.
        String token = resolveToken(request);

        // 2. 토큰이 유효한지 검사합니다. (validateToken)
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {

            // 3. 토큰이 유효하면, 토큰에서 Claims를 추출합니다.
            var claims = jwtTokenProvider.getClaimsFromToken(token);

            Long userId = claims.get("userId", Long.class);
            String role = claims.get("role", String.class);

            // 4. Claims에서 권한(role) 정보를 가져와, Security가 이해할 수 있는 Authority로 변환합니다.
            var authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

            // 5. Authentication 객체를 생성하여, SecurityContext에 저장(setAuthentication)합니다.
            // (여기서 Principal은 User 객체 대신 userId(Long)를 사용해도 무방합니다)
            Authentication authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 다음 필터로 요청을 전달합니다.
        filterChain.doFilter(request, response);
    }

    // Request Header에서 토큰 정보(Bearer)를 추출하는 헬퍼 메소드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}