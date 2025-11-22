package com.plyy.plyyReboot.domain.preference.genre;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubGenreRepository extends JpaRepository<SubGenre, Long> {
    List<SubGenre> findAllByParentGenreId(Long parentGenreId);
    boolean existsByNameContaining(String name);
}
