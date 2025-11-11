package com.plyy.plyyReboot.config.security;

import com.plyy.plyyReboot.client.oauth.CustomOAuth2UserService;
import com.plyy.plyyReboot.client.oauth.OAuth2AuthenticationSuccessHandler;
import com.plyy.plyyReboot.config.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 필터 체인 1: API (JWT)
     * /api/** 경로의 모든 요청을 담당
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**") // (1) /api/로 시작하는 요청만 이 필터가 처리
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // (2) API 엔드포인트별 권한 설정 (새 온보딩 규칙 추가)
                .authorizeHttpRequests(auth -> auth
                        // (permitAll() 경로 - 토큰 재발급)
                        .requestMatchers("/api/v1/auth/refresh").permitAll()

                        // (온보딩 규칙 -  닉네임 중복 확인)
                        .requestMatchers("/api/v1/users/nickname/check").hasAnyRole("NEW_USER", "USER", "CURATOR")

                        // (온보딩 규칙 - 온보딩 완료)
                        .requestMatchers("/api/v1/users/onboarding/complete").hasRole("NEW_USER")

                        // (기존 규칙 - 큐레이터/일반 유저)
                        .requestMatchers("/api/v1/curator/**").hasRole("CURATOR")
                        .requestMatchers("/api/v1/**").hasAnyRole("USER", "CURATOR") // (로그아웃 등 나머지 /api/v1/**)
                        .anyRequest().authenticated()
                )

                // (3) JWT 필터를 Security 필터 체인에 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // (4) API 인증/인가 실패 시 로그인 페이지 대신 401/403 반환
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler((req, res, e) -> res.setStatus(HttpStatus.FORBIDDEN.value()))
                );

        return http.build();
    }

    /**
     * 필터 체인 2: 웹 (OAuth2 로그인)
     * /api/** 를 제외한 나머지 모든 요청을 담당
     */
    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)

                // (1) 웹 경로 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/static/**",
                                "/index.html",
                                "/docs/**",                 // API 문서
                                "/actuator/**",             // 모니터링
                                "/login/oauth2/code/**",    // OAuth 리다이렉트
                                "/oauth2/authorization/**" // OAuth 로그인 시도
                        ).permitAll()
                        .anyRequest().authenticated() // (그 외 혹시 모를 웹 경로는 인증 필요)
                )

                // (2) OAuth2 로그인 설정 (웹용 필터체인에만 적용)
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                );

        return http.build();
    }

    // (CORS 설정 Bean - 공통 사용)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://127.0.0.1:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setExposedHeaders(Arrays.asList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}