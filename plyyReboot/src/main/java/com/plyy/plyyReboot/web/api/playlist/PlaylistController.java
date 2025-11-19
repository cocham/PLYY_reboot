package com.plyy.plyyReboot.web.api.playlist;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plyy.plyyReboot.web.api.playlist.dto.PlaylistCreateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/v1/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createPlaylist(
            @AuthenticationPrincipal Long userId,
            @RequestPart("request") String requestJson,
            @RequestPart("coverImage") MultipartFile coverImage
    ) {
        PlaylistCreateRequest request;
        try {
            request = objectMapper.readValue(requestJson, PlaylistCreateRequest.class);
        } catch (JsonProcessingException e) {
            log.warn("JSON 파싱 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }

        Long playlistId = playlistService.createPlaylist(request, coverImage, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/api/v1/playlists/" + playlistId))
                .build();
    }
}
