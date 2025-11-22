package com.plyy.plyyReboot.domain.playlist.exception;

import com.plyy.plyyReboot.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.plyy.plyyReboot.web.api.playlist")
public class PlaylistExceptionHandler {

    @ExceptionHandler(GenreNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGenreNotFound(GenreNotFoundException e) {
        log.warn("장르 조회 실패 {}: {}", e.getDetailInfo(), e.getMessage());
        return buildResponse(e, HttpStatus.BAD_REQUEST); // 또는 NOT_FOUND
    }

    @ExceptionHandler(SubGenreNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSubGenreNotFound(SubGenreNotFoundException e) {
        log.warn("하위 장르 조회 실패 {}: {}", e.getDetailInfo(), e.getMessage());
        return buildResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MoodNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMoodNotFound(MoodNotFoundException e) {
        log.warn("무드 조회 실패 {}: {}", e.getDetailInfo(), e.getMessage());
        return buildResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPlaylistUrlException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPlaylistUrl(InvalidPlaylistUrlException e) {
        log.warn("잘못된 URL 형식 {}: {}", e.getDetailInfo(), e.getMessage());
        return buildResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedPlaylistSourceException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedSource(UnsupportedPlaylistSourceException e) {
        log.warn("지원하지 않는 소스 {}: {}", e.getDetailInfo(), e.getMessage());
        return buildResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPlaylistIdException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPlaylistId(InvalidPlaylistIdException e) {
        log.warn("유효하지 않은 ID {}: {}", e.getDetailInfo(), e.getMessage());
        return buildResponse(e, HttpStatus.BAD_REQUEST);
    }

    // 공통 응답 생성 메서드
    private ResponseEntity<ErrorResponse> buildResponse(BasePlaylistException e, HttpStatus status) {
        ErrorResponse response = new ErrorResponse(
                e.getErrorCode(),
                e.getMessage()
        );
        return ResponseEntity.status(status).body(response);
    }
}