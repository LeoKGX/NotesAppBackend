package com.leokgx.NotesAppBackend.controller;

import com.leokgx.NotesAppBackend.dto.NoteDTO;
import com.leokgx.NotesAppBackend.model.Note;
import com.leokgx.NotesAppBackend.model.Tag;
import com.leokgx.NotesAppBackend.model.User;
import com.leokgx.NotesAppBackend.repository.INoteRepo;
import com.leokgx.NotesAppBackend.repository.ITagRepo;
import com.leokgx.NotesAppBackend.repository.IUserRepo;
import com.leokgx.NotesAppBackend.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
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
    @Autowired
    NoteService noteService;

    @PostMapping("/save")
    public ResponseEntity<?> saveNote(@RequestBody @Valid NoteDTO noteDTO, Principal principal) {
        return  noteService.saveNote(noteDTO, principal);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateNote(@PathVariable Long id, @RequestBody @Valid NoteDTO noteData, Principal principal) {
        return noteService.updateNote(id, noteData, principal);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable Long id, Principal principal){
        return noteService.deleteNote(id, principal);
    }

    @GetMapping("/getnotes")
    public ResponseEntity<List<NoteDTO>> getNotes(Principal principal){
        return noteService.getNotes(principal);
    }

    @GetMapping("/unarchived")
    public ResponseEntity<List<NoteDTO>> getActiveNotes(Principal principal) {
        return noteService.getActiveNotes(principal);
    }

    @GetMapping("/archived")
    public ResponseEntity<List<NoteDTO>> getArchivedNotes(Principal principal) {
        return noteService.getArchivedNotes(principal);
    }


    @PutMapping("/archive/{id}")
    public ResponseEntity<?> archiveNote(@PathVariable Long id, Principal principal) {
        return noteService.archiveNote(id, principal);
    }

    @PutMapping("/unarchive/{id}")
    public ResponseEntity<?> unarchiveNote(@PathVariable Long id, Principal principal) {
        return noteService.unarchiveNote(id, principal);
    }


    @GetMapping("/filter")
    public ResponseEntity<List<Note>> getNotesByTag(@RequestParam String tag, Principal principal) {
        User user = userRepo.findByusername(principal.getName()).orElseThrow();

        List<Note> notes = noteRepo.findByUserAndTags_Name(user, tag);
        return ResponseEntity.ok(notes);
    }

    @PutMapping("/{noteId}/add-tag")
    public ResponseEntity<String> addTagToNote(@PathVariable Long noteId, @RequestParam String tag,Principal principal) {
        return noteService.addTagToNote(noteId, tag, principal);
    }

    @PutMapping("/{noteId}/remove-tag")
    public ResponseEntity<String> removeTagFromNote(@PathVariable Long noteId, @RequestParam String tag, Principal principal) {
        return noteService.removeTagFromNote(noteId, tag, principal);
    }


}


