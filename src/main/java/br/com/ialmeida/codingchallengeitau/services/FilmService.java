package br.com.ialmeida.codingchallengeitau.services;

import br.com.ialmeida.codingchallengeitau.clients.FilmClient;
import br.com.ialmeida.codingchallengeitau.entities.*;
import br.com.ialmeida.codingchallengeitau.entities.enums.Profile;
import br.com.ialmeida.codingchallengeitau.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FilmService {

    private final FilmRepository filmRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentResponseRepository commentResponseRepository;
    private final FilmClient filmClient;
    private final UserService userService;

    public List<Film> findAll() {
        return filmRepository.findAll();
    }

    public Film findById(Long id) {
        return filmRepository.findById(id).orElseThrow(() -> new RuntimeException("Film with id = '" + id + "' not found."));
    }

    public List<Film> findByTitle(String title) {
        List<Film> films = filmRepository.findByTitleContainingIgnoreCase(title);

        if (films.isEmpty()) {
            Film apiFilm = filmClient.findByTitle(title);
            films.add(this.insert(new Film(null, apiFilm.getTitle(), apiFilm.getGenre(), apiFilm.getDirector(), apiFilm.getWriter())));
        }

        return films;
    }

    public void rating(Long filmId, Long userId, Double score) {
        Film film = this.findById(filmId);
        User user = this.updateUserScore(userService.findById(userId));

        ratingRepository.save(new Rating(null, film, user, score));
    }

    public void comment(Long filmId, Long userId, String message) {
        User user = userService.findById(userId);
        if (user.getProfile().equals(Profile.READER)) {
            throw new RuntimeException("You cannot comment with profile = '" + user.getProfile() + "'.");
        }

        Film film = this.findById(filmId);
        user = this.updateUserScore(user);

        commentRepository.save(new Comment(null, film, user, message));
    }

    public void commentResponse(Long commentId, Long userId, String message) {
        User user = userService.findById(userId);
        if (user.getProfile().equals(Profile.READER)) {
            throw new RuntimeException("You cannot reply to a comment with profile = '" + user.getProfile() + "'.");
        }

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment with id = '" + commentId + "' not found."));
        this.updateUserScore(user);

        commentResponseRepository.save(new CommentResponse(null, comment, message));
    }

    private User updateUserScore(User user) {
        user.addScore();
        return userRepository.save(user);
    }

    private Film insert(Film film) {
        return filmRepository.save(film);
    }

}
