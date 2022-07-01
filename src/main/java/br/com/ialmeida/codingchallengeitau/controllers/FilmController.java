package br.com.ialmeida.codingchallengeitau.controllers;

import br.com.ialmeida.codingchallengeitau.entities.Film;
import br.com.ialmeida.codingchallengeitau.services.FilmService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
            Authentication authentication,
            @RequestParam(value = "filmId", defaultValue = "") Long filmId,
            @RequestParam(value = "score", defaultValue = "") Double score
    ) {
        filmService.rating(filmId, String.valueOf(authentication.getPrincipal()), score);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/comment")
    public ResponseEntity<Void> comment(
            Authentication authentication,
            @RequestParam(value = "filmId", defaultValue = "") Long filmId,
            @RequestParam(value = "message", defaultValue = "") String message
    ) {
        filmService.comment(filmId, String.valueOf(authentication.getPrincipal()), message);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/commentResponse")
    public ResponseEntity<Void> commentResponse(
            Authentication authentication,
            @RequestParam(value = "commentId", defaultValue = "") Long commentId,
            @RequestParam(value = "message", defaultValue = "") String message
    ) {
        filmService.commentResponse(commentId, String.valueOf(authentication.getPrincipal()), message);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/quoteComment")
    public ResponseEntity<Void> quoteComment(
            Authentication authentication,
            @RequestParam(value = "filmId", defaultValue = "") Long filmId,
            @RequestParam(value = "commentId", defaultValue = "") Long commentId,
            @RequestParam(value = "message", defaultValue = "") String message
    ) {
        filmService.quoteComment(filmId, commentId, String.valueOf(authentication.getPrincipal()), message);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/react")
    public ResponseEntity<Void> react(
            Authentication authentication,
            @RequestParam(value = "commentId", defaultValue = "") Long commentId,
            @RequestParam(value = "reaction", defaultValue = "") Boolean reaction
    ) {
        filmService.react(commentId, String.valueOf(authentication.getPrincipal()), reaction);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/deleteComment")
    public ResponseEntity<Void> deleteComment(
            Authentication authentication,
            @RequestParam(value = "commentId", defaultValue = "") Long commentId
    ) {
        filmService.deleteComment(commentId, String.valueOf(authentication.getPrincipal()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/setRepeatedComment")
    public ResponseEntity<Void> setRepeatedComment(
            Authentication authentication,
            @RequestParam(value = "commentId", defaultValue = "") Long commentId
    ) {
        filmService.setRepeatedComment(commentId, String.valueOf(authentication.getPrincipal()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/makeModerator")
    public ResponseEntity<Void> makeModerator(
            Authentication authentication,
            @RequestParam(value = "userId", defaultValue = "") Long userId
    ) {
        filmService.makeModerator(userId, String.valueOf(authentication.getPrincipal()));
        return ResponseEntity.noContent().build();
    }

}
