package br.com.ialmeida.codingchallengeitau.controllers;

import br.com.ialmeida.codingchallengeitau.clients.FilmClient;
import br.com.ialmeida.codingchallengeitau.entities.Film;
import br.com.ialmeida.codingchallengeitau.services.FilmService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
@AllArgsConstructor
public class FilmController {

    private final FilmService filmService;
    private final FilmClient filmClient;

    @GetMapping(value = "/films")
    public ResponseEntity<List<Film>> findAll() {
        return ResponseEntity.ok().body(filmService.findAll());
    }

    @GetMapping(value = "/films/{id}")
    public ResponseEntity<Film> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(filmService.findById(id));
    }

    @GetMapping(value = "/film")
    public ResponseEntity<Object> findByName(@RequestParam(value = "name") String name) {
        return ResponseEntity.ok().body(filmClient.findFilmByName(name));
    }

}
