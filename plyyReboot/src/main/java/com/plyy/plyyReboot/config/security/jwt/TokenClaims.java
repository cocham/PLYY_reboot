package com.plyy.plyyReboot.config.security.jwt;

import com.plyy.plyyReboot.client.oauth.exception.InvalidRoleException;
import com.plyy.plyyReboot.domain.user.exception.UserNotFoundException;
import io.jsonwebtoken.Claims;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

/**
 * JWT 토큰의 Claims를 타입 안전하게 접근하기 위한 래퍼 클래스
 * 매직 스트링을 제거하고 타입 안전성을 보장
 */
@Getter
@RequiredArgsConstructor
public class TokenClaims {

    private static final String USER_ID_CLAIM = "userId";
    private static final String ROLE_CLAIM = "role";

    private final Claims claims;

    /**
     * 사용자 ID 추출
     * @return 사용자 ID
     * @throws UserNotFoundException userId claim이 없거나 변환 불가능한 경우
     */
    public Long getUserId() {
        Number userId = claims.get(USER_ID_CLAIM, Number.class);
        if (userId == null) {
            throw new UserNotFoundException("해당 유저를 찾을 수 없습니다.");
        }
        return userId.longValue();
    }

    /**
     * 사용자 권한(Role) 추출
     * @return 권한 문자열
     * @throws InvalidRoleException role claim이 없는 경우
     */
    public String getRole() {
        String role = claims.get(ROLE_CLAIM, String.class);
        if (role == null || role.isBlank()) {
            throw new InvalidRoleException("Role은 필수 값입니다.");
        }
        return role;
    }

    /**
     * 토큰 만료 시간
     */
    public Date getExpiration() {
        return claims.getExpiration();
    }

    /**
     * 토큰 발급 시간
     */
    public Date getIssuedAt() {
        return claims.getIssuedAt();
    }

    /**
     * 토큰 남은 유효 시간(밀리초)
     */
    public long getRemainingMillis() {
        Date expiration = getExpiration();
        return Math.max(0L, expiration.getTime() - System.currentTimeMillis());
    }

    /**
     * 토큰이 만료되었는지 확인
     */
    public boolean isExpired() {
        return getExpiration().before(new Date());
    }

    /**
     * 원본 Claims 객체 반환 (필요시 직접 접근)
     */
    public Claims getRawClaims() {
        return claims;
    }
}
