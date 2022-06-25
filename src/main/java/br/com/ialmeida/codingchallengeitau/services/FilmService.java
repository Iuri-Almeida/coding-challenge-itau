package br.com.ialmeida.codingchallengeitau.services;

import br.com.ialmeida.codingchallengeitau.clients.FilmClient;
import br.com.ialmeida.codingchallengeitau.entities.*;
import br.com.ialmeida.codingchallengeitau.entities.enums.Profile;
import br.com.ialmeida.codingchallengeitau.exceptions.*;
import br.com.ialmeida.codingchallengeitau.repositories.*;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class FilmService {

    @Value("${api.key}")
    private String apiKey;

    private final FilmRepository filmRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentResponseRepository commentResponseRepository;
    private final ReactionRepository reactionRepository;
    private final FilmClient filmClient;
    private final UserService userService;

    public FilmService(FilmRepository filmRepository, RatingRepository ratingRepository, UserRepository userRepository, CommentRepository commentRepository, CommentResponseRepository commentResponseRepository, ReactionRepository reactionRepository, FilmClient filmClient, UserService userService) {
        this.filmRepository = filmRepository;
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.commentResponseRepository = commentResponseRepository;
        this.reactionRepository = reactionRepository;
        this.filmClient = filmClient;
        this.userService = userService;
    }

    public List<Film> findAll() {
        return filmRepository.findAll();
    }

    public Film findById(Long id) {
        return filmRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Film with id = '" + id + "' not found."));
    }

    public List<Film> findByTitle(String title) {
        this.validateParams(title);

        List<Film> films = filmRepository.findByTitleContainingIgnoreCase(title);

        if (films.isEmpty()) {
            Film apiFilm = filmClient.findByTitle(title, this.apiKey);
            if (apiFilm.getTitle() == null && apiFilm.getGenre() == null && apiFilm.getDirector() == null && apiFilm.getWriter() == null) {
                throw new ResourceNotFoundException("Film with title = " + title + "' not found.");
            }

            films.add(this.insert(new Film(null, apiFilm.getTitle(), apiFilm.getGenre(), apiFilm.getDirector(), apiFilm.getWriter())));
        }

        return films;
    }

    public void rating(Long filmId, String token, Double score) {
        this.validateParams(filmId, token, score);

        User user = this.getUserByJwtToken(token);

        Film film = this.findById(filmId);
        for (Rating r : film.getRatings()) {
            if (r.getUser().equals(user)) {
                throw new DuplicatedActionException("User with email = '" + user.getEmail() + "' has already rated the film.");
            }
        }

        user = this.updateUserScore(user);

        ratingRepository.save(new Rating(null, film, user, score));
    }

    public void comment(Long filmId, String token, String message) {
        this.validateParams(filmId, token, message);

        User user = this.getUserByJwtToken(token);
        if (user.getProfile().equals(Profile.READER)) {
            throw new ProfileBlockException("You cannot comment with profile = '" + user.getProfile() + "'.");
        }

        Film film = this.findById(filmId);
        user = this.updateUserScore(user);

        commentRepository.save(new Comment(null, film, user, message));
    }

    public void commentResponse(Long commentId, String token, String message) {
        this.validateParams(commentId, token, message);

        User user = this.getUserByJwtToken(token);
        if (user.getProfile().equals(Profile.READER)) {
            throw new ProfileBlockException("You cannot reply to a comment with profile = '" + user.getProfile() + "'.");
        }

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment with id = '" + commentId + "' not found."));
        user = this.updateUserScore(user);

        commentResponseRepository.save(new CommentResponse(null, user, comment, message));
    }

    public void react(Long commentId, String token, Boolean reaction) {
        this.validateParams(commentId, token, reaction);

        User user = this.getUserByJwtToken(token);
        if (user.getProfile().equals(Profile.READER) || user.getProfile().equals(Profile.BASIC)) {
            throw new ProfileBlockException("You cannot react to a comment with profile = '" + user.getProfile() + "'.");
        }

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment with id = '" + commentId + "' not found."));
        for (Reaction r : comment.getReactions()) {
            if (r.getUser().equals(user)) {
                r.setReaction(reaction);
                reactionRepository.save(r);
                return;
            }
        }

        reactionRepository.save(new Reaction(null, user, comment, reaction));
    }

    public void deleteComment(Long commentId, String token) {
        this.validateParams(commentId, token);

        User user = this.getUserByJwtToken(token);
        if (user.getProfile().equals(Profile.READER) || user.getProfile().equals(Profile.BASIC) || user.getProfile().equals(Profile.ADVANCED)) {
            throw new ProfileBlockException("You cannot delete a comment with profile = '" + user.getProfile() + "'.");
        }

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment with id = '" + commentId + "' not found."));

        try {
            commentResponseRepository.deleteAll(comment.getCommentResponses());
            reactionRepository.deleteAll(comment.getReactions());

            commentRepository.delete(comment);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Error deleting comment. Could not found all resources");
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Resource with dependencies in database");
        }
    }

    public void setRepeatedComment(Long commentId, String token) {
        this.validateParams(commentId, token);

        User user = this.getUserByJwtToken(token);
        if (user.getProfile().equals(Profile.READER) || user.getProfile().equals(Profile.BASIC) || user.getProfile().equals(Profile.ADVANCED)) {
            throw new ProfileBlockException("You cannot set a comment as repeated with profile = '" + user.getProfile() + "'.");
        }

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment with id = '" + commentId + "' not found."));
        comment.setIsRepeated(true);

        commentRepository.save(comment);
    }

    private void validateParams(String title) {
        if (Objects.equals(title, "")) {
            throw new NullParameterException("You cannot search with null parameters.");
        }
    }

    private void validateParams(Long id1, String token, Double score) {
        if (id1 == null || Objects.equals(token, "") || score == null) {
            throw new NullParameterException("You cannot rate with null parameters.");
        }
    }

    private void validateParams(Long id1, String token, String message) {
        if (id1 == null || Objects.equals(token, "") || Objects.equals(message, "")) {
            throw new NullParameterException("You cannot comment with null parameters.");
        }
    }

    private void validateParams(Long id1, String token, Boolean reaction) {
        if (id1 == null || Objects.equals(token, "") || reaction == null) {
            throw new NullParameterException("You cannot react with null parameters.");
        }
    }

    private void validateParams(Long id1, String token) {
        if (id1 == null || Objects.equals(token, "")) {
            throw new NullParameterException("You cannot delete or set a comment as repeated with null parameters.");
        }
    }

    private User updateUserScore(User user) {
        user.addScore();
        return userRepository.save(user);
    }

    private Film insert(Film film) {
        return filmRepository.save(film);
    }

    private User getUserByJwtToken(String token) {
        try {
            token = token.replace("Bearer ", "");
            String email = JWT.require(Algorithm.HMAC512("d42b825e-a76a-41b4-ae99-c229ca400103"))
                    .build()
                    .verify(token)
                    .getSubject();
            return userService.findByEmail(email);
        } catch (SignatureVerificationException e) {
            throw new RuntimeException("You must use a valid JWT token.");
        }
    }

}
