package br.com.ialmeida.codingchallengeitau.controllers;

import br.com.ialmeida.codingchallengeitau.entities.User;
import br.com.ialmeida.codingchallengeitau.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok().body(userService.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(userService.findById(id));
    }

    @PostMapping
    public ResponseEntity<User> insert(@RequestBody User user) {
        return ResponseEntity.ok().body(userService.insert(user));
    }

}
