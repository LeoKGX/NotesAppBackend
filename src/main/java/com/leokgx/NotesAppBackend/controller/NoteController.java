package com.leokgx.NotesAppBackend.controller;

import com.leokgx.NotesAppBackend.model.Note;
import com.leokgx.NotesAppBackend.model.Tag;
import com.leokgx.NotesAppBackend.model.User;
import com.leokgx.NotesAppBackend.repository.INoteRepo;
import com.leokgx.NotesAppBackend.repository.ITagRepo;
import com.leokgx.NotesAppBackend.repository.IUserRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notes")
public class NoteController {
    @Autowired
    INoteRepo noteRepo;
    @Autowired
    IUserRepo userRepo;
    @Autowired
    ITagRepo tagRepo;

    @PostMapping("/save")
    public ResponseEntity<?> createNote(@RequestBody @Valid Note note, Principal principal) {
        String username = principal.getName();
        Optional<User> userOpt = userRepo.findByusername(username);
        if (userOpt.isPresent()) {
            note.setUser(userOpt.get());
            List<String> incomingTagNames = note.getTags().stream()
                    .map(Tag::getName)
                    .toList();

            // Reutilizar o crear los tags correspondientes
            Set<Tag> resolvedTags = findOrCreateTags(incomingTagNames);

            // Asociar los tags a la nota
            note.setTags(resolvedTags);
            Note saved = noteRepo.save(note);
            return ResponseEntity.ok(saved);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateNote(@PathVariable Long id, @RequestBody @Valid Note noteData, Principal principal) {
        String username = principal.getName();
        Optional<User> userOpt = userRepo.findByusername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        User user = userOpt.get();
        Optional<Note> optionalNote = noteRepo.findById(id);
        if (optionalNote.isPresent()) {
            Note existingNote = optionalNote.get();
            if (!existingNote.getUser().equals(user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not allowed to edit this note");
            }
            existingNote.setTitle(noteData.getTitle());
            existingNote.setContent(noteData.getContent());
            List<String> incomingTagNames = noteData.getTags().stream()
                    .map(Tag::getName)
                    .toList();

            Set<Tag> resolvedTags = findOrCreateTags(incomingTagNames);
            existingNote.setTags(resolvedTags);

            noteRepo.save(existingNote);
            return ResponseEntity.ok("Note updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable Long id){
        noteRepo.deleteById(id);
        return ResponseEntity.ok("Note deleted");
    }

    @GetMapping("/getnotes")
    public ResponseEntity<List<Note>> getNotes(Principal principal){
        User user = userRepo.findByusername(principal.getName()).orElseThrow();
        List<Note> userNotes = noteRepo.findByUser(user);
        return ResponseEntity.ok(userNotes);
    }

    @GetMapping("/unarchived")
    public ResponseEntity<List<Note>> getActiveNotes(Principal principal) {
        User user = userRepo.findByusername(principal.getName()).orElseThrow();
        List<Note> activeNotes = noteRepo.findByUserAndArchived(user, false);
        return ResponseEntity.ok(activeNotes);
    }

    @GetMapping("/archived")
    public ResponseEntity<List<Note>> getArchivedNotes(Principal principal) {
        User user = userRepo.findByusername(principal.getName()).orElseThrow();
        List<Note> archivedNotes = noteRepo.findByUserAndArchived(user, true);
        return ResponseEntity.ok(archivedNotes);
    }


    @PutMapping("/archive/{id}")
    public ResponseEntity<String> archiveNote(@PathVariable Long id, Principal principal) {
        User user = userRepo.findByusername(principal.getName()).orElseThrow();
        Optional<Note> optionalNote = noteRepo.findById(id);
        if (optionalNote.isPresent() && optionalNote.get().getUser().equals(user)) {
            Note note = optionalNote.get();
            note.setArchived(true);
            noteRepo.save(note);
            return ResponseEntity.ok("Note archived");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
        }
    }

    @PutMapping("/unarchive/{id}")
    public ResponseEntity<String> unarchiveNote(@PathVariable Long id, Principal principal) {
        User user = userRepo.findByusername(principal.getName()).orElseThrow();
        Optional<Note> optionalNote = noteRepo.findById(id);
        if (optionalNote.isPresent() && optionalNote.get().getUser().equals(user)) {
            Note note = optionalNote.get();
            note.setArchived(false);
            noteRepo.save(note);
            return ResponseEntity.ok("Note unarchived");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
        }
    }


    @GetMapping("/filter")
    public ResponseEntity<List<Note>> getNotesByTag(@RequestParam String tag, Principal principal) {
        User user = userRepo.findByusername(principal.getName()).orElseThrow();

        List<Note> notes = noteRepo.findByUserAndTags_Name(user, tag);
        return ResponseEntity.ok(notes);
    }

    @PutMapping("/{noteId}/add-tag")
    public ResponseEntity<String> addTagToNote(
            @PathVariable Long noteId,
            @RequestParam String tag,
            Principal principal
    ) {
        User user = userRepo.findByusername(principal.getName()).orElseThrow();
        Note note = noteRepo.findById(noteId).orElse(null);

        if (note == null || !note.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
        }

        Tag tagEntity = tagRepo.findByName(tag).orElseGet(() -> {
            Tag newTag = new Tag();
            newTag.setName(tag);
            return tagRepo.save(newTag);
        });

        note.getTags().add(tagEntity);
        noteRepo.save(note);

        return ResponseEntity.ok("Tag added to note");
    }

    @PutMapping("/{noteId}/remove-tag")
    public ResponseEntity<String> removeTagFromNote(
            @PathVariable Long noteId,
            @RequestParam String tag,
            Principal principal
    ) {
        User user = userRepo.findByusername(principal.getName()).orElseThrow();
        Note note = noteRepo.findById(noteId).orElse(null);

        if (note == null || !note.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
        }

        Optional<Tag> tagOpt = tagRepo.findByName(tag);
        if (tagOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tag not found");
        }

        boolean removed = note.getTags().remove(tagOpt.get());
        if (!removed) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tag not associated with note");
        }

        noteRepo.save(note);
        return ResponseEntity.ok("Tag removed from note");
    }

    private Set<Tag> findOrCreateTags(List<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames) {
            Tag tag = tagRepo.findByName(tagName)
                    .orElseGet(() -> tagRepo.save(new Tag(null, tagName, null)));
            tags.add(tag);
        }
        return tags;
    }
}


