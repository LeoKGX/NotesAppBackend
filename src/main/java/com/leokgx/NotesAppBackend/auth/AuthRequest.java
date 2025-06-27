package com.leokgx.NotesAppBackend.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

public class AuthRequest {
    @NotNull @Length(min = 6, max = 20)
    @Getter @Setter
    private String username;

    @NotNull @Length(min = 8, max = 64)
    @Getter @Setter
    private String password;

}
