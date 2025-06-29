package com.leokgx.NotesAppBackend.service;

import com.leokgx.NotesAppBackend.dto.NoteDTO;
import com.leokgx.NotesAppBackend.dto.TagDTO;
import com.leokgx.NotesAppBackend.mapper.NoteMapper;
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
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;

@Service
public class NoteService {
    @Autowired
    private INoteRepo noteRepo;
    @Autowired
    private IUserRepo userRepo;
    @Autowired
    private ITagRepo tagRepo;
    @Autowired
    private NoteMapper noteMapper;

    public ResponseEntity<?> saveNote(NoteDTO noteDTO, Principal principal) {
        String username = principal.getName();
        Optional<User> userOpt = userRepo.findByusername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userOpt.get();

        // Convierte DTO a entidad para persistir
        Note note = noteMapper.toEntity(noteDTO);
        note.setUser(user);

        // Manejar tags: reutilizar o crear
        List<String> incomingTagNames = noteDTO.getTags().stream()
                .map(TagDTO::getName)
                .toList();
        Set<Tag> resolvedTags = findOrCreateTags(incomingTagNames);
        note.setTags(resolvedTags);

        Note savedNote = noteRepo.save(note);

        // Convierte entidad guardada a DTO para respuesta
        NoteDTO savedNoteDTO = noteMapper.toDTO(savedNote);
        return ResponseEntity.ok(savedNoteDTO);
    }

        public ResponseEntity<HttpStatus> updateNote(Long id, NoteDTO noteDTO, Principal principal) {
            String username = principal.getName();
            Optional<User> userOpt = userRepo.findByusername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            User user = userOpt.get();
            Optional<Note> optionalNote = noteRepo.findById(id);
            if (optionalNote.isPresent()) {
                Note existingNote = optionalNote.get();
                if (!existingNote.getUser().equals(user)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                existingNote.setTitle(noteDTO.getTitle());
                existingNote.setContent(noteDTO.getContent());
                existingNote.setArchived(noteDTO.isArchived());

                List<String> incomingTagNames = noteDTO.getTags().stream()
                        .map(TagDTO::getName)
                        .toList();

                Set<Tag> resolvedTags = findOrCreateTags(incomingTagNames);
                existingNote.setTags(resolvedTags);

                noteRepo.save(existingNote);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }

        public ResponseEntity<?> deleteNote( Long id, Principal principal){
            User user = getUserFromPrincipal(principal);
            Optional<Note> optionalNote = noteRepo.findById(id);

            if (optionalNote.isEmpty() || !optionalNote.get().getUser().equals(user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to delete this note");
            }

            noteRepo.deleteById(id);
            return ResponseEntity.ok().build();
        }

        public ResponseEntity<List<NoteDTO>> getNotes(Principal principal){
            User user = userRepo.findByusername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Note> userNotes = noteRepo.findByUser(user);

            // Mapea lista de entidades a lista de DTOs
            List<NoteDTO> noteDTOs = userNotes.stream()
                    .map(noteMapper::toDTO)
                    .toList();

            return ResponseEntity.ok(noteDTOs);
        }

        public ResponseEntity<List<NoteDTO>> getActiveNotes(Principal principal) {
            User user = getUserFromPrincipal(principal);
            List<Note> activeNotes = noteRepo.findByUserAndArchived(user, false);

            List<NoteDTO> activeNoteDTOs = activeNotes.stream()
                    .map(noteMapper::toDTO)
                    .toList();

            return ResponseEntity.ok(activeNoteDTOs);
        }

        public ResponseEntity<List<NoteDTO>> getArchivedNotes(Principal principal) {
            User user = getUserFromPrincipal(principal);
            List<Note> archivedNotes = noteRepo.findByUserAndArchived(user, true);

            List<NoteDTO> archivedNoteDTOs = archivedNotes.stream()
                    .map(noteMapper::toDTO)
                    .toList();

            return ResponseEntity.ok(archivedNoteDTOs);
        }


        public ResponseEntity<String> archiveNote( Long id, Principal principal) {
            User user = getUserFromPrincipal(principal);
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

        public ResponseEntity<String> unarchiveNote( Long id, Principal principal) {
            User user = getUserFromPrincipal(principal);
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

        public ResponseEntity<List<NoteDTO>> getNotesByTag( String tag, Principal principal) {
            User user = getUserFromPrincipal(principal);
            List<Note> notes = noteRepo.findByUserAndTags_Name(user, tag);

            List<NoteDTO> noteDTOs = notes.stream()
                    .map(noteMapper::toDTO)
                    .toList();

            return ResponseEntity.ok(noteDTOs);
        }

        public ResponseEntity<String> addTagToNote(Long noteId, String tag, Principal principal
    ) {
            User user = getUserFromPrincipal(principal);
            Note note = getUserNoteOrThrow(noteId, user);

            Tag tagEntity = tagRepo.findByName(tag).orElseGet(() -> {
                Tag newTag = new Tag();
                newTag.setName(tag);
                return tagRepo.save(newTag);
            });

            note.getTags().add(tagEntity);
            noteRepo.save(note);

            return ResponseEntity.ok("Tag added to note");
        }

        public ResponseEntity<String> removeTagFromNote(Long noteId, String tag, Principal principal) {
            User user = getUserFromPrincipal(principal);
            Note note = getUserNoteOrThrow(noteId, user);

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


    private User getUserFromPrincipal(Principal principal) {
        return userRepo.findByusername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Note getUserNoteOrThrow(Long noteId, User user) {
        return noteRepo.findById(noteId)
                .filter(note -> note.getUser().equals(user))
                .orElseThrow(() -> new RuntimeException("Note not found or access denied"));
    }

    private Set<Tag> findOrCreateTags(List<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames) {
            if (tagName == null || tagName.trim().isEmpty()) continue;

            String cleanName = tagName.trim().toLowerCase();
            Tag tag = tagRepo.findByName(cleanName)
                    .orElseGet(() -> tagRepo.save(new Tag(null, cleanName, new HashSet<>())));

            tags.add(tag);
        }
        return tags;
    }
}
