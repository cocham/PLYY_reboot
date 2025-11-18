package com.plyy.plyyReboot.web.api.playlist.dto.genre;

import com.plyy.plyyReboot.web.api.playlist.dto.genre.dto.GenreResponse;
import com.plyy.plyyReboot.web.api.playlist.dto.genre.dto.SubGenreResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    // 1. 마스터 장르 목록 조회
    @GetMapping("/master")
    public ResponseEntity<List<GenreResponse>> getMasterGenres() {
        return ResponseEntity.ok(genreService.getAllMasterGenres());
    }

    // 2. 특정 마스터 장르의 서브 장르 조회
    @GetMapping("/{masterGenreId}/sub-genres")
    public ResponseEntity<List<SubGenreResponse>> getSubGenres(
            @PathVariable Long masterGenreId
    ) {
        return ResponseEntity.ok(genreService.getSubGenres(masterGenreId));
    }
}