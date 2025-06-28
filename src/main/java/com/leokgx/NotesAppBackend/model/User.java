package com.leokgx.NotesAppBackend.model;
/**
 *
 * @author Leo Kaltenbach
 */
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Entity
@Table(name = "\"user\"")
public class User implements UserDetails {
    @Getter @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Setter
    @Column(nullable = false, unique = true, length = 20, name = "username")
    @Length(min = 6, max = 20)
    private String username;

    @Setter
    @Column(nullable = false, length = 64, name = "password")
    @Length(min = 8, max = 64)
    private String password;

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public User() {}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

}
