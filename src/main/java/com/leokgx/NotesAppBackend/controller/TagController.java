package com.leokgx.NotesAppBackend.controller;

import com.leokgx.NotesAppBackend.dto.TagDTO;
import com.leokgx.NotesAppBackend.model.Tag;
import com.leokgx.NotesAppBackend.repository.ITagRepo;
import com.leokgx.NotesAppBackend.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class TagController {
    @Autowired
    ITagRepo tagRepo;
    @Autowired
    TagService tagService;
    @GetMapping("/tags")
    public ResponseEntity<List<TagDTO>> getAllTags() {
        return tagService.getNotes();
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
