package com.plyy.plyyReboot.config.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.time.Duration;

/**
 * JWT 관련 설정값을 관리하는 Properties 클래스
 * application.yml의 spring.jwt 하위 설정을 바인딩
 */
@Getter
@Setter
@Component
@Validated
@ConfigurationProperties(prefix = "spring.jwt")
public class JwtProperties {

    /**
     * JWT 서명에 사용할 시크릿 키
     */
    @NotBlank(message = "JWT secret은 필수입니다")
    private String secret;

    /**
     * Access Token 만료 시간 (24시간)
     */
    @Positive
    private long accessTokenExpirationMs = Duration.ofHours(24).toMillis();

    /**
     * Refresh Token 만료 시간 (7일)
     */
    @Positive
    private long refreshTokenExpirationMs = Duration.ofDays(7).toMillis();

    /**
     * 토큰을 전달할 쿠키 이름 (accessToken)
     */
    private String cookieName = "accessToken";

    /**
     * 토큰 발행자 (Issuer)
     */
    private String issuer = "plyy-api";

    public Duration getAccessTokenExpiration() {
        return Duration.ofMillis(accessTokenExpirationMs);
    }

    public Duration getRefreshTokenExpiration() {
        return Duration.ofMillis(refreshTokenExpirationMs);
    }
}
