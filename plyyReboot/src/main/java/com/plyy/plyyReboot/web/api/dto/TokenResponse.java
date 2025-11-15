package com.plyy.plyyReboot.web.api.dto;

/**
 * API 응답으로 토큰 쌍을 반환하기 위한 DTO
 */
public record TokenResponse(String accessToken, String refreshToken) {
}
