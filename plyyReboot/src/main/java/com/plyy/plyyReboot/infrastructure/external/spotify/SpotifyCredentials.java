package com.plyy.plyyReboot.infrastructure.external.spotify;

import com.plyy.plyyReboot.infrastructure.external.spotify.exception.InvalidSpotifyCredentialsException;

import java.util.Base64;

/**
 * Spotify 인증 정보 Value Object
 */
record SpotifyCredentials(String clientId, String clientSecret) {
    public SpotifyCredentials {
        if (clientId == null || clientId.isBlank()) {
            throw new InvalidSpotifyCredentialsException("Spotify Client ID가 설정되지 않았습니다.");
        }
        if (clientSecret == null || clientSecret.isBlank()) {
            throw new InvalidSpotifyCredentialsException("Spotify Client Secret이 설정되지 않았습니다.");
        }
    }

    public String encodeToBase64() {
        String credentials = clientId + ":" + clientSecret;
        return Base64.getEncoder().encodeToString(credentials.getBytes());
    }
}
