package com.plyy.plyyReboot.infrastructure.external.spotify.exception;

import com.plyy.plyyReboot.infrastructure.common.exception.BaseAdapterException;

/**
 * Spotify 인증 및 토큰 발급 실패 시 발생하는 예외
 */
public class SpotifyAuthenticationException extends BaseAdapterException {

    public SpotifyAuthenticationException(String message) {
        super(message);
    }

    public SpotifyAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}