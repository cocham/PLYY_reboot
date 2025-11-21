package com.plyy.plyyReboot.web.api.auth;

import com.plyy.plyyReboot.client.oauth.util.CookieUtil;
import com.plyy.plyyReboot.config.security.jwt.JwtProperties;
import com.plyy.plyyReboot.config.security.jwt.JwtTokenGenerator;
import com.plyy.plyyReboot.config.security.jwt.TokenExtractor;
import com.plyy.plyyReboot.config.security.jwt.TokenService;
import com.plyy.plyyReboot.config.security.jwt.exception.TokenRefreshException;
import com.plyy.plyyReboot.web.api.auth.dto.LogoutResponse;
import com.plyy.plyyReboot.web.api.auth.dto.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 및 토큰 관리 API 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final TokenService tokenService;
    private final TokenExtractor tokenExtractor;
    private final JwtProperties jwtProperties;


    /**
     * Access Token 재발급 (Rotation 적용)
     * - 요청: 쿠키에 담긴 Refresh Token
     * - 성공 응답: Body(새 Access Token), Cookie(새 Refresh Token)
     * - 실패 응답: 401 Unauthorized + 쿠키 삭제
     */
    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissueAccessToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = tokenExtractor.extractRefreshToken(request)
                .orElse(null);

        if (refreshToken == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            JwtTokenGenerator.TokenPair newTokens = tokenService.refreshTokens(refreshToken);

            CookieUtil.addCookie(
                    request,
                    response,
                    jwtProperties.getRefreshTokenCookieName(),
                    newTokens.refreshToken(),
                    jwtProperties.getRefreshTokenExpiration(), // Duration
                    true // HttpOnly
            );

            return ResponseEntity.ok(new TokenResponse(newTokens.accessToken()));

        } catch (TokenRefreshException e) {
            log.warn("토큰 재발급 실패: {}", e.getMessage());

            CookieUtil.deleteCookie(
                    request,
                    response,
                    jwtProperties.getRefreshTokenCookieName()
            );

            return ResponseEntity.status(401).build();
        }
    }

    /**
     * logout
     * - 로그아웃은 인증된 사용자만 가능하므로 토큰이 필수임.
     * - 토큰이 없다면 필터에서 걸러지거나, 여기서 예외가 발생해야 함.
     */
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(
            @AuthenticationPrincipal Long userId, // 인증된 유저만 들어옴
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        log.debug("로그아웃 요청: userId={}", userId);

        String accessToken = tokenExtractor.extractAccessTokenOrThrow(request);

        tokenService.revokeTokens(userId, accessToken);

        CookieUtil.deleteCookie(
                request,
                response,
                jwtProperties.getRefreshTokenCookieName()
        );

        return ResponseEntity.ok(new LogoutResponse("로그아웃되었습니다."));
    }
}
