package com.leokgx.NotesAppBackend.controller;

import com.leokgx.NotesAppBackend.model.User;
import com.leokgx.NotesAppBackend.repository.IUserRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class UserController {
    @Autowired
    IUserRepo userRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @PostMapping("/crear/usuario")
    public ResponseEntity<User> crearUser(@RequestBody @Valid User user){
        String encodedPass = passwordEncoder.encode(user.getPassword());
        user.setPass(encodedPass);
        userRepo.save(user);
        URI userUri = URI.create(user.getUsername());

        return ResponseEntity.created(userUri).body(user);
    }
}
