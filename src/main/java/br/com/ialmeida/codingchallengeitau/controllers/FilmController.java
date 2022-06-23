package br.com.ialmeida.codingchallengeitau.controllers;

import br.com.ialmeida.codingchallengeitau.entities.Film;
import br.com.ialmeida.codingchallengeitau.entities.Rating;
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

    @GetMapping(value = "/films")
    public ResponseEntity<List<Film>> findAll() {
        return ResponseEntity.ok().body(filmService.findAll());
    }

    @GetMapping(value = "/film")
    public ResponseEntity<List<Film>> findByName(@RequestParam(value = "name") String name) {
        return ResponseEntity.ok().body(filmService.findByTitle(name));
    }

    @GetMapping(value = "/rating")
    public ResponseEntity<Void> rating(
            @RequestParam(value = "filmId") Long filmId,
            @RequestParam(value = "userId") Long userId,
            @RequestParam(value = "score") Double score
    ) {
        filmService.rating(filmId, userId, score);
        return ResponseEntity.noContent().build();
    }

}
