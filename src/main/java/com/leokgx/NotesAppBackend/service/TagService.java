package com.leokgx.NotesAppBackend.service;

import com.leokgx.NotesAppBackend.dto.TagDTO;
import com.leokgx.NotesAppBackend.mapper.TagMapper;
import com.leokgx.NotesAppBackend.model.Tag;
import com.leokgx.NotesAppBackend.repository.ITagRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    @Autowired
    ITagRepo tagRepo;
    @Autowired
    TagMapper tagMapper;

    public ResponseEntity<List<TagDTO>> getNotes() {

        List<Tag> tags = tagRepo.findAll();

        // Mapea lista de entidades a lista de DTOs
        List<TagDTO> TagDTOs = tags.stream()
                .map(tagMapper::toDTO)
                .toList();

        return ResponseEntity.ok(TagDTOs);
    }
}