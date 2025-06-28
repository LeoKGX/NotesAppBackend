package com.leokgx.NotesAppBackend.controller;

import com.leokgx.NotesAppBackend.model.Tag;
import com.leokgx.NotesAppBackend.repository.ITagRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TagController {
    @Autowired
    ITagRepo tagRepo;
    @GetMapping("/tags")
    public ResponseEntity<List<Tag>> getAllTags() {
        return ResponseEntity.ok(tagRepo.findAll());
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
