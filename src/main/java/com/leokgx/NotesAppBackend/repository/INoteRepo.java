package com.leokgx.NotesAppBackend.repository;

import com.leokgx.NotesAppBackend.model.Note;
import com.leokgx.NotesAppBackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface INoteRepo extends JpaRepository<Note, Long> {
    Optional<Note> findByid(Long id);
    List<Note> findByUser(User user);
    List<Note> findByUserAndArchived(User user, boolean archived);
    List<Note> findByUserAndTags_Name(User user, String tagName);
}
