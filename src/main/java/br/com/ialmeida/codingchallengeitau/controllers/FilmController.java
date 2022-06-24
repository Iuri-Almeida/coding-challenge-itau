package br.com.ialmeida.codingchallengeitau.controllers;

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

    @GetMapping(value = "/films")
    public ResponseEntity<List<Film>> findAll() {
        return ResponseEntity.ok().body(filmService.findAll());
    }

    @GetMapping(value = "/film")
    public ResponseEntity<List<Film>> findByName(@RequestParam(value = "name", defaultValue = "") String name) {
        return ResponseEntity.ok().body(filmService.findByTitle(name));
    }

    @GetMapping(value = "/rating")
    public ResponseEntity<Void> rating(
            @RequestParam(value = "filmId", defaultValue = "") Long filmId,
            @RequestParam(value = "userId", defaultValue = "") Long userId,
            @RequestParam(value = "score", defaultValue = "") Double score
    ) {
        filmService.rating(filmId, userId, score);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/comment")
    public ResponseEntity<Void> comment(
            @RequestParam(value = "filmId", defaultValue = "") Long filmId,
            @RequestParam(value = "userId", defaultValue = "") Long userId,
            @RequestParam(value = "message", defaultValue = "") String message
    ) {
        filmService.comment(filmId, userId, message);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/commentResponse")
    public ResponseEntity<Void> commentResponse(
            @RequestParam(value = "commentId", defaultValue = "") Long commentId,
            @RequestParam(value = "userId", defaultValue = "") Long userId,
            @RequestParam(value = "message", defaultValue = "") String message
    ) {
        filmService.commentResponse(commentId, userId, message);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/react")
    public ResponseEntity<Void> react(
            @RequestParam(value = "commentId", defaultValue = "") Long commentId,
            @RequestParam(value = "userId", defaultValue = "") Long userId,
            @RequestParam(value = "reaction", defaultValue = "") Boolean reaction
    ) {
        filmService.react(commentId, userId, reaction);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/deleteComment")
    public ResponseEntity<Void> deleteComment(
            @RequestParam(value = "commentId", defaultValue = "") Long commentId,
            @RequestParam(value = "userId", defaultValue = "") Long userId
    ) {
        filmService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/setRepeatedComment")
    public ResponseEntity<Void> setRepeatedComment(
            @RequestParam(value = "commentId", defaultValue = "") Long commentId,
            @RequestParam(value = "userId", defaultValue = "") Long userId
    ) {
        filmService.setRepeatedComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

}
