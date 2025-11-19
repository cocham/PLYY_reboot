package com.plyy.plyyReboot.web.api.playlist.dto;

import jakarta.validation.constraints.NotNull;

public record TrackCurationRequest(
        @NotNull
        Integer trackIndex, // 0-based index
        String introduction
) {}