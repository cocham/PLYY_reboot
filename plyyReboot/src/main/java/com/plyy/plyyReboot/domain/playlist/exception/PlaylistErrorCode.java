package com.plyy.plyyReboot.domain.playlist.exception;

import com.plyy.plyyReboot.exception.ErrorCodeInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlaylistErrorCode implements ErrorCodeInterface {

    GENRE_NOT_FOUND("PLAYLIST_001", "해당 장르를 찾을 수 없습니다."),
    SUB_GENRE_NOT_FOUND("PLAYLIST_002", "해당 하위 장르를 찾을 수 없습니다."),
    MOOD_NOT_FOUND("PLAYLIST_003", "해당 무드를 찾을 수 없습니다."),
    TAG_NOT_FOUND("PLAYLIST_004", "해당 태그를 찾을 수 없습니다."),
    INVALID_PLAYLIST_URL("PLAYLIST_005", "유효하지 않은 플레이리스트 URL 형식입니다."),
    UNSUPPORTED_PLAYLIST_SOURCE("PLAYLIST_006", "지원하지 않는 플레이리스트 소스입니다."),
    INVALID_PLAYLIST_ID("PLAYLIST_007", "플레이리스트 ID가 유효하지 않습니다."),
    PLAYLIST_FETCH_FAILED("PLAYLIST_008", "외부 플레이리스트 정보를 가져오는 데 실패했습니다."),
    INVALID_PLAYLIST_TITLE("PLAYLIST_009", "플레이리스트 제목이 유효하지 않습니다.");

    private final String code;
    private final String message;
}