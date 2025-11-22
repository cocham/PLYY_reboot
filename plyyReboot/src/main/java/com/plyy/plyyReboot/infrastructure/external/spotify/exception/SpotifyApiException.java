package com.plyy.plyyReboot.infrastructure.external.spotify.exception;

import com.plyy.plyyReboot.infrastructure.common.exception.BaseAdapterException;

public class SpotifyApiException extends BaseAdapterException {

    public SpotifyApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpotifyApiException(String message) {
        super(message);
    }
}