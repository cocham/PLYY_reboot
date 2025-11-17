package com.plyy.plyyReboot.config.security.jwt;

import com.plyy.plyyReboot.config.security.jwt.exception.*;
import com.plyy.plyyReboot.config.security.redis.RedisService;
import com.plyy.plyyReboot.config.security.redis.exception.RedisOperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 토큰 발급 및 관리를 담당하는 서비스
 * 토큰 생성, Redis 저장, 갱신, 무효화 등 토큰 관련 모든 비즈니스 로직 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenGenerator jwtTokenGenerator;
    private final JwtTokenParser jwtTokenParser;
    private final RedisService redisService;

    /**
     * 새로운 토큰 쌍 발급 (회원가입, 온보딩 완료 시)
     *
     * @param userId 사용자 ID
     * @param role 사용자 권한
     * @return 발급된 토큰 쌍
     * @throws TokenIssuanceException 토큰 발급 실패 시
     */
    @Transactional
    public JwtTokenGenerator.TokenPair issueTokens(Long userId, String role) {
        log.debug("토큰 발급 시작: userId={}, role={}", userId, role);

        try {
            var tokens = jwtTokenGenerator.generateTokenPair(userId, role);
            redisService.saveRefreshToken(userId, tokens.refreshToken());

            log.info("토큰 발급 완료: userId={}", userId);
            return tokens;

        } catch (RedisOperationException e) {
            log.error("토큰 발급 실패 (Redis 오류): userId={}", userId, e);
            throw new TokenIssuanceException("토큰 발급에 실패했습니다. 잠시 후 다시 시도해주세요.", e);
        } catch (Exception e) {
            log.error("토큰 발급 실패 (예상치 못한 오류): userId={}", userId, e);
            throw new TokenIssuanceException("토큰 발급 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * Refresh Token을 사용하여 새로운 Access Token 발급
     *
     * @param refreshToken 기존 Refresh Token
     * @return 새로운 토큰 쌍
     * @throws TokenRefreshException 토큰 갱신 실패 시
     */
    @Transactional
    public JwtTokenGenerator.TokenPair refreshTokens(String refreshToken) {
        log.debug("토큰 갱신 시작");

        TokenClaims claims = jwtTokenParser.parse(refreshToken)
                .orElseThrow(() -> {
                    log.warn("토큰 갱신 실패: 유효하지 않은 Refresh Token");
                    return new TokenRefreshException("유효하지 않은 Refresh Token입니다.");
                });

        Long userId = claims.getUserId();
        String role = claims.getRole();

        try {
            redisService.validateRefreshToken(userId, refreshToken);

            var newTokens = jwtTokenGenerator.generateTokenPair(userId, role);
            redisService.saveRefreshToken(userId, newTokens.refreshToken());

            log.info("토큰 갱신 완료: userId={}", userId);
            return newTokens;
        } catch (RefreshTokenNotFoundException | RefreshTokenMismatchException e) {
            log.warn("토큰 갱신 실패: {}", e.getMessage());
            throw new TokenRefreshException(e.getMessage(), e);
        } catch (Exception e) {
            log.error("토큰 갱신 실패: userId={}", userId, e);
            throw new TokenRefreshException("토큰 갱신에 실패했습니다.", e);
        }
    }

    /**
     * 토큰 무효화 (로그아웃 시)
     *
     * @param userId 사용자 ID
     * @param accessToken 무효화할 Access Token
     */
    @Transactional
    public void revokeTokens(Long userId, String accessToken) {
        log.debug("토큰 무효화 시작: userId={}", userId);

        try {
            boolean deleted = redisService.deleteRefreshToken(userId);
            if (deleted) {
                log.debug("Refresh Token 삭제 완료: userId={}", userId);
            }

            jwtTokenParser.extractRemainingMillis(accessToken).ifPresent(remainingMs -> {
                if (remainingMs > 0) {
                    redisService.addToDenylist(accessToken, remainingMs);
                    log.debug("Access Token 거부 목록 추가: userId={}, 남은시간={}ms", userId, remainingMs);
                }
            });

            log.info("토큰 무효화 완료: userId={}", userId);

        } catch (Exception e) {
            log.error("토큰 무효화 실패: userId={}", userId, e);
        }
    }

    /**
     * 사용자의 모든 세션 무효화 (비밀번호 변경, 계정 정지 등)
     *
     * @param userId 사용자 ID
     */
    @Transactional
    public void revokeAllUserSessions(Long userId) {
        log.info("모든 세션 무효화 시작: userId={}", userId);

        try {
            redisService.deleteRefreshToken(userId);
            log.info("모든 세션 무효화 완료: userId={}", userId);
        } catch (Exception e) {
            log.error("세션 무효화 실패: userId={}", userId, e);
            throw new TokenRevocationException("세션 무효화에 실패했습니다.", e);
        }
    }

    /**
     * 토큰 유효성 검증
     *
     * @param token 검증할 토큰
     * @return 유효하면 true
     */
    public boolean validateToken(String token) {
        return jwtTokenParser.validate(token);
    }

    /**
     * 토큰이 거부 목록에 있는지 확인
     *
     * @param token 확인할 토큰
     * @return 거부 목록에 있으면 true
     */
    public boolean isTokenRevoked(String token) {
        return redisService.isTokenInDenylist(token);
    }
}
