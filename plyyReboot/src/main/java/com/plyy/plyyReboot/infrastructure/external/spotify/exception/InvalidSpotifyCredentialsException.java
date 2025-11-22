package com.plyy.plyyReboot.infrastructure.external.spotify.exception;

import com.plyy.plyyReboot.infrastructure.common.exception.BaseAdapterException;

/**
 * Spotify Client ID 또는 Secret 설정이 누락되었을 때 발생하는 예외
 */
public class InvalidSpotifyCredentialsException extends BaseAdapterException {

    public InvalidSpotifyCredentialsException(String message) {
        super(message);
    }
}
