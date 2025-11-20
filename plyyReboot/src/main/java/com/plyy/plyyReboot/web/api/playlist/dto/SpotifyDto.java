package com.plyy.plyyReboot.web.api.playlist.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class SpotifyDto {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SpotifyTokenResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("expires_in") int expiresIn
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SpotifyPlaylistResponse(
            String id,
            String name,
            String description,
            List<SpotifyImage> images,
            SpotifyTrackCollection tracks
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SpotifyTrackCollection(
            List<SpotifyTrackItem> items
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SpotifyTrackItem(
            SpotifyTrack track
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SpotifyTrack(
            String id,
            String name,
            @JsonProperty("duration_ms") int durationMs,
            SpotifyAlbum album,
            List<SpotifyArtist> artists
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SpotifyArtist(
            String name
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SpotifyAlbum(
            String name,
            List<SpotifyImage> images
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SpotifyImage(
            String url,
            Integer height,
            Integer width
    ) {}
}
