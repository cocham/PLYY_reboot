package com.plyy.plyyReboot.client.oauth;

import com.plyy.plyyReboot.client.oauth.util.CookieUtil;
import com.plyy.plyyReboot.config.security.jwt.JwtTokenGenerator;
import com.plyy.plyyReboot.config.security.jwt.JwtProperties;
import com.plyy.plyyReboot.domain.user.User;
import com.plyy.plyyReboot.domain.user.UserRepository;
import com.plyy.plyyReboot.config.security.redis.RedisService;
import com.plyy.plyyReboot.domain.user.exception.UserNotFoundException;

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

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        User user = getUserFromAuthentication(authentication);
        var tokens = issueTokensAndSaveToRedis(user);
        addRefreshTokenToCookie(request, response, tokens);
        String targetUrl = determineTargetUrl(user);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private User getUserFromAuthentication(Authentication authentication) {
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Email: " + email));
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

    private void addRefreshTokenToCookie(
            HttpServletRequest request,
            HttpServletResponse response,
            JwtTokenGenerator.TokenPair tokens
    ) {
        CookieUtil.addCookie(
                request,
                response,
                jwtProperties.getRefreshTokenCookieName(),
                tokens.refreshToken(),
                jwtProperties.getRefreshTokenExpiration(), // Duration
                true // HttpOnly
        );
    }

    private String determineTargetUrl(User user) {
        boolean isNewUser = "ROLE_NEW_USER".equals(user.getRole());
        String baseUrl = isNewUser ? FRONTEND_ONBOARDING_URL : FRONTEND_CALLBACK_URL;

        return UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("isNewUser", isNewUser)
                .build()
                .toUriString();
    }
}
