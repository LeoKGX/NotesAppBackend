package com.leokgx.NotesAppBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class TagDTO {
    @Getter @Setter
    private Long id;

    @Getter @Setter
    private String name;

}
