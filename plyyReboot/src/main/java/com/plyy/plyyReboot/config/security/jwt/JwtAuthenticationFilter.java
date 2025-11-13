package com.plyy.plyyReboot.config.security.jwt;

import com.plyy.plyyReboot.config.security.redis.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT 토큰 기반 인증 필터
 * 요청에서 토큰을 추출하여 검증하고, SecurityContext에 인증 정보 설정
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenExtractor tokenExtractor;
    private final JwtTokenParser tokenParser;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. 요청에서 토큰 추출
        tokenExtractor.extract(request).ifPresent(token -> {

            // 2. 거부 목록 확인 (로그아웃된 토큰)
            if (redisService.isTokenInDenylist(token)) {
                log.debug("거부 목록의 토큰 감지: 인증 거부");
                return;
            }

            // 3. 토큰 파싱 및 검증
            tokenParser.parse(token).ifPresent(claims -> {
                try {
                    // 4. 인증 객체 생성 및 SecurityContext 설정
                    Authentication authentication = createAuthentication(claims);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("JWT 인증 성공: userId={}", claims.getUserId());

                } catch (Exception e) {
                    log.warn("인증 객체 생성 실패: {}", e.getMessage());
                }
            });
        });

        filterChain.doFilter(request, response);
    }

    /**
     * TokenClaims로부터 Spring Security Authentication 객체 생성
     */
    private Authentication createAuthentication(TokenClaims claims) {
        Long userId = claims.getUserId();
        String role = claims.getRole();

        var authorities = Collections.singletonList(
                new SimpleGrantedAuthority(role)
        );

        return new UsernamePasswordAuthenticationToken(
                userId,      // principal
                null,        // credentials (JWT에서는 불필요)
                authorities  // authorities
        );
    }
}