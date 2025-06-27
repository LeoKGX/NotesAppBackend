package com.leokgx.NotesAppBackend.auth;

import lombok.Getter;
import lombok.Setter;

public class AuthResponse {
    @Getter @Setter
    private String accessToken;

    public AuthResponse(){}

    public AuthResponse(String accessToken){
        this.accessToken = accessToken;
    }

}
