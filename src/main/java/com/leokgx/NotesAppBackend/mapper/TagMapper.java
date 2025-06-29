package com.leokgx.NotesAppBackend.mapper;

import com.leokgx.NotesAppBackend.dto.TagDTO;
import com.leokgx.NotesAppBackend.model.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagMapper {
    public TagDTO toDTO(Tag tag) {
        TagDTO dto = new TagDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        return dto;
    }

    public Tag toEntity(TagDTO dto) {
        Tag tag = new Tag();
        tag.setId(dto.getId()); // o null si lo maneja la DB
        tag.setName(dto.getName());
        return tag;
    }
}
