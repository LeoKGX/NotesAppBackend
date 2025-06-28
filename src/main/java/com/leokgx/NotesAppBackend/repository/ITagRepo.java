package com.leokgx.NotesAppBackend.repository;

import com.leokgx.NotesAppBackend.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ITagRepo extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
}
