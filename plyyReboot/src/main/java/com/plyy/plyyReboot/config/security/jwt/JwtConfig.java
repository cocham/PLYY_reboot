package com.plyy.plyyReboot.config.security.jwt;

import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * JWT 관련 빈을 설정하는 Configuration 클래스
 */
@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    private final JwtProperties jwtProperties;
    private final Integer BYTE = 64;
    /**
     * JWT 서명에 사용할 SecretKey 생성
     * HS512 알고리즘 사용을 위해 최소 512비트(64바이트) 필요
     */
    @Bean
    public SecretKey jwtSecretKey() {
        String secret = jwtProperties.getSecret();

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < BYTE) {
            throw new IllegalArgumentException(
                    "JWT secret은 최소 64바이트(512비트) 이상이어야 합니다. 현재: " + keyBytes.length + "바이트"
            );
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }
}