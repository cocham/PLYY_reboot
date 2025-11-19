package com.plyy.plyyReboot.domain.preference;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    boolean existsByNameContaining(String name);
}
