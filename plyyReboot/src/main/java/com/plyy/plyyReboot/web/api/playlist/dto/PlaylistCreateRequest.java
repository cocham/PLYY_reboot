package com.plyy.plyyReboot.web.api.playlist.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PlaylistCreateRequest(
        @NotEmpty
        String source, // "spotify" | "youtube"

        @NotEmpty
        String playlistUrl,

        @NotEmpty
        String title,

        String introduction,

        @NotNull
        Long masterGenreId,

        List<Long> subGenreIds,
        List<Long> moodIds,
        List<String> tags,
        List<TrackCurationRequest> trackCurations
) {}