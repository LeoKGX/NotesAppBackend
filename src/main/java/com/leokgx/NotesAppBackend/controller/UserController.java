package com.leokgx.NotesAppBackend.controller;

import com.leokgx.NotesAppBackend.model.User;
import com.leokgx.NotesAppBackend.repository.IUserRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
public class UserController {
    @Autowired
    IUserRepo userRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @PostMapping("/register")
    public ResponseEntity<?> crearUser(@RequestBody @Valid User user){
            String encodedPass = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPass);
            userRepo.save(user);
            URI userUri = URI.create(user.getUsername());
            return ResponseEntity.created(userUri).body(user);
    }

    @DeleteMapping("/delete/user")
    public ResponseEntity<?> deleteUser(@RequestBody @Valid User user){
        userRepo.delete(user);
        return ResponseEntity.ok("User deleted");
    }

}
