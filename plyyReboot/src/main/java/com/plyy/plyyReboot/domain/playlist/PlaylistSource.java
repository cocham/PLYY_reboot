package com.plyy.plyyReboot.domain.playlist;

import com.plyy.plyyReboot.domain.playlist.exception.InvalidPlaylistUrlException;

public enum PlaylistSource {
    SPOTIFY {
        @Override
        public String extractIdFromUrl(String url) {
            try {
                String[] parts = url.split("/");
                String lastPart = parts[parts.length - 1];
                return lastPart.split("\\?")[0];
            } catch (Exception e) {
                throw new InvalidPlaylistUrlException("유효하지 않은 스포티파이 URL입니다: " + url, e);
            }
        }
    };
//    },
//
//    YOUTUBE {
//        @Override
//        public String extractIdFromUrl(String url) {
//            try {
//                if (!url.contains("list=")) {
//                    throw new InvalidPlaylistUrlException(url, "유튜브 재생목록 URL에는 'list' 파라미터가 필수입니다.");
//                }
//
//                String[] splitByList = url.split("list=");
//                String afterListParam = splitByList[1];
//                return afterListParam.split("[&#]")[0];
//            } catch (InvalidPlaylistUrlException e) {
//                throw e;
//            } catch (Exception e) {
//                throw new InvalidPlaylistUrlException("유효하지 않은 유튜브 URL입니다: " + url, e);
//            }
//        }
//    };

    public abstract String extractIdFromUrl(String url);
}
