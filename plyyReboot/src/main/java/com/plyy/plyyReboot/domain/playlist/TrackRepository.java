package com.plyy.plyyReboot.domain.playlist;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrackRepository extends JpaRepository<Track, Long> {
    Optional<Track> findByTitleAndArtist(String title, String artist);
    Optional<Track> findBySpotifyId(String spotifyId);
}
