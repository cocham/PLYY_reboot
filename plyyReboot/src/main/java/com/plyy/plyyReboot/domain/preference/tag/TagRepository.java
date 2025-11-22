package com.plyy.plyyReboot.domain.preference.tag;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByNameAndType(String name, TagType type);
    Optional<Tag> findByName(String name);
}
