package com.leokgx.NotesAppBackend.mapper;

import com.leokgx.NotesAppBackend.dto.NoteDTO;
import com.leokgx.NotesAppBackend.dto.TagDTO;
import com.leokgx.NotesAppBackend.model.Note;
import com.leokgx.NotesAppBackend.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class NoteMapper {
    @Autowired
    private TagMapper tagMapper;

    // De entidad a DTO
    public NoteDTO toDTO(Note note) {
        if (note == null) return null;

        List<TagDTO> tagDTOs = note.getTags() != null
                ? note.getTags().stream().map(tagMapper::toDTO).collect(Collectors.toList())
                : null;

        return new NoteDTO(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getArchived(),
                tagDTOs
        );
    }

    // De DTO a entidad
    public Note toEntity(NoteDTO noteDTO) {
        if (noteDTO == null) return null;

        List<Tag> tags = noteDTO.getTags() != null
                ? noteDTO.getTags().stream().map(tagMapper::toEntity).toList()
                : null;

        Note note = new Note();
        note.setId(noteDTO.getId());
        note.setTitle(noteDTO.getTitle());
        note.setContent(noteDTO.getContent());
        note.setArchived(noteDTO.isArchived());
        note.setTags(tags != null ? Set.copyOf(tags) : null);

        return note;
    }
}
