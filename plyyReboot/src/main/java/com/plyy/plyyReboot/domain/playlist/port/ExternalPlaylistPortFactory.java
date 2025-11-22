package com.plyy.plyyReboot.domain.playlist.port;

import com.plyy.plyyReboot.domain.playlist.PlaylistId;
import com.plyy.plyyReboot.domain.playlist.PlaylistSource;
import com.plyy.plyyReboot.domain.playlist.exception.UnsupportedPlaylistSourceException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 포트 구현체를 선택하는 팩토리
 */
@Component
public class ExternalPlaylistPortFactory {

    private final Map<PlaylistSource, ExternalPlaylistPort> portMap;

    public ExternalPlaylistPortFactory(List<ExternalPlaylistPort> ports) {
        this.portMap = ports.stream()
                .filter(port -> port.getSupportedSource() != null)
                .collect(Collectors.toMap(ExternalPlaylistPort::getSupportedSource, Function.identity()));
    }

    public ExternalPlaylistPort getPort(PlaylistId playlistId) {
        ExternalPlaylistPort port = portMap.get(playlistId.getSource());
        if (port == null) {
            throw new UnsupportedPlaylistSourceException(playlistId.getSource().name());
        }
        return port;
    }
}
