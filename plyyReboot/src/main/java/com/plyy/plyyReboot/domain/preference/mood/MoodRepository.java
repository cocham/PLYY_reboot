package com.plyy.plyyReboot.domain.preference;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MoodRepository extends JpaRepository<Mood, Long> {
    boolean existsByNameContaining(String name);
}
