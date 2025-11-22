package com.plyy.plyyReboot.domain.playlist;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrackRepository extends JpaRepository<Track, Long> {
    List<Track> findAllBySpotifyIdIn(List<String> spotifyIds);
}
