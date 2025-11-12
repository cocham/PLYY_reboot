package com.plyy.plyyReboot.config.security.jwt;

import com.plyy.plyyReboot.config.security.RedisService;
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
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String token = resolveToken(request);

        if (StringUtils.hasText(token) && redisService.isTokenInDenylist(token)) {
            logger.debug("블랙리스트에 등록된 토큰이 감지되어 SecurityContext를 초기화합니다.");
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }

        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            var claims = jwtTokenProvider.getClaimsFromToken(token);

            Long userId = claims.get("userId", Long.class);
            String role = claims.get("role", String.class);

            var authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
            Authentication authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("accessToken"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}