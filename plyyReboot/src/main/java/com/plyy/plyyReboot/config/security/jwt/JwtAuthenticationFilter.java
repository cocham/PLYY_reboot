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
import java.util.Arrays;
import java.util.Collections;
import jakarta.servlet.http.Cookie;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Request "Cookie"에서 "accessToken" 토큰을 꺼냄.
        String token = resolveToken(request);

        // 2. 토큰이 유효한지 검사 (validateToken)
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {

            // 3. 토큰이 유효하면, 토큰에서 Claims를 추출
            var claims = jwtTokenProvider.getClaimsFromToken(token);

            Long userId = claims.get("userId", Long.class);
            String role = claims.get("role", String.class);

            // 4. Claims에서 권한(role) 정보를 가져와, Security가 이해할 수 있는 Authority로 변환
            var authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

            // 5. Authentication 객체를 생성하여, SecurityContext에 저장(setAuthentication)
            // (여기서 Principal은 User 객체 대신 userId(Long)를 사용해도 무방)
            Authentication authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 다음 필터로 요청을 전달
        filterChain.doFilter(request, response);
    }

    // Request Header에서 토큰 정보(Bearer)를 추출하는 헬퍼 메소드
    private String resolveToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        // 쿠키 배열을 스트림으로 변환하여 "accessToken" 쿠키를 찾음
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("accessToken"))
                .findFirst() // 찾으면
                .map(Cookie::getValue) // 그 값을 반환
                .orElse(null); // 없으면 null
    }
}