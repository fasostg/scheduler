package br.com.projeto.scheduler.user;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository repository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody User user) {
        User userFound = this.repository.findByUsername(user.getUsername());
        if (userFound != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already created!");
        }

        String passHashed = BCrypt.withDefaults().hashToString(12, user.getPassword().toCharArray());
        user.setPassword(passHashed);

        var userCreated = repository.save(user);
        return ResponseEntity.ok(userCreated);
    }

}
