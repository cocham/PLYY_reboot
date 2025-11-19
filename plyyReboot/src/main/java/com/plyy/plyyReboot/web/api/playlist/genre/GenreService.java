package com.plyy.plyyReboot.web.api.playlist.genre;

import com.plyy.plyyReboot.domain.preference.GenreRepository;
import com.plyy.plyyReboot.domain.preference.SubGenreRepository;
import com.plyy.plyyReboot.web.api.playlist.genre.dto.GenreResponse;
import com.plyy.plyyReboot.web.api.playlist.genre.dto.SubGenreResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenreService {

    private final GenreRepository genreRepository;
    private final SubGenreRepository subGenreRepository;

    // 모든 마스터 장르 가져오기
    public List<GenreResponse> getAllMasterGenres() {
        return genreRepository.findAll().stream()
                .map(g -> new GenreResponse(g.getId(), g.getName()))
                .toList();
    }

    // 마스터 ID에 해당하는 서브 장르들 가져오기
    public List<SubGenreResponse> getSubGenres(Long masterId) {
        return subGenreRepository.findAllByParentGenreId(masterId).stream()
                .map(sg -> new SubGenreResponse(sg.getId(), sg.getName()))
                .toList();
    }
}