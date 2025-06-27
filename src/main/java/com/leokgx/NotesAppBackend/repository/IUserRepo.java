package com.leokgx.NotesAppBackend.repository;

import com.leokgx.NotesAppBackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepo extends JpaRepository<User, Long> {
    Optional<User> findByusername(String user);
    public User findByid(Long id);
}
