package com.plyy.plyyReboot.client.oauth;

import com.plyy.plyyReboot.client.oauth.util.CookieUtil;
import com.plyy.plyyReboot.config.security.jwt.JwtTokenGenerator;
import com.plyy.plyyReboot.config.security.jwt.JwtProperties;
import com.plyy.plyyReboot.domain.user.User;
import com.plyy.plyyReboot.domain.user.UserRepository;
import com.plyy.plyyReboot.config.security.redis.RedisService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenGenerator jwtTokenGenerator;
    private final UserRepository userRepository;
    private final RedisService redisService;
    private final JwtProperties jwtProperties;

    private static final String FRONTEND_CALLBACK_URL = "http://localhost:3000/auth/callback";
    private static final String FRONTEND_ONBOARDING_URL = "http://localhost:3000/onboarding";
    private static final int ACCESS_TOKEN_COOKIE_MAX_AGE = 86400;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        User user = getUserFromAuthentication(authentication);
        var tokens = issueTokensAndSaveToRedis(user);
        addTokensToCookie(request, response, tokens);
        String targetUrl = determineTargetUrl(user);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private User getUserFromAuthentication(Authentication authentication) {
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "인증 성공했으나 DB에 유저가 없음. Email: " + email
                ));
    }

    private JwtTokenGenerator.TokenPair issueTokensAndSaveToRedis(User user) {
        var tokens = jwtTokenGenerator.generateTokenPair(user.getId(), user.getRole());

        try {
            redisService.saveRefreshToken(user.getId(), tokens.refreshToken());
            log.info("Refresh Token 저장 성공 - UserID: {}", user.getId());
        } catch (Exception e) {
            log.error("Refresh Token 저장 실패 - UserID: {}, 로그인은 계속 진행됨", user.getId(), e);
        }

        return tokens;
    }

    private void addTokensToCookie(
            HttpServletRequest request,
            HttpServletResponse response,
            JwtTokenGenerator.TokenPair tokens
    ) {

        // Access Token 쿠키 설정
        CookieUtil.addCookie(
                request,
                response,
                jwtProperties.getCookieName(),
                tokens.accessToken(),
                ACCESS_TOKEN_COOKIE_MAX_AGE,
                false
        );
    }

    private String determineTargetUrl(User user) {
        boolean isNewUser = "ROLE_NEW_USER".equals(user.getRole());

        if (isNewUser) {
            return UriComponentsBuilder.fromUriString(FRONTEND_ONBOARDING_URL)
                    .queryParam("isNewUser", true)
                    .build()
                    .toUriString();
        } else {
            return UriComponentsBuilder.fromUriString(FRONTEND_CALLBACK_URL)
                    .build()
                    .toUriString();
        }
    }
}