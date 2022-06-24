package br.com.ialmeida.codingchallengeitau.services;

import br.com.ialmeida.codingchallengeitau.clients.FilmClient;
import br.com.ialmeida.codingchallengeitau.entities.*;
import br.com.ialmeida.codingchallengeitau.entities.enums.Profile;
import br.com.ialmeida.codingchallengeitau.exceptions.*;
import br.com.ialmeida.codingchallengeitau.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class FilmService {

    private final FilmRepository filmRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentResponseRepository commentResponseRepository;
    private final ReactionRepository reactionRepository;
    private final FilmClient filmClient;
    private final UserService userService;

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
            Film apiFilm = filmClient.findByTitle(title);
            if (apiFilm.getTitle() == null && apiFilm.getGenre() == null && apiFilm.getDirector() == null && apiFilm.getWriter() == null) {
                throw new ResourceNotFoundException("Film with title = " + title + "' not found.");
            }

            films.add(this.insert(new Film(null, apiFilm.getTitle(), apiFilm.getGenre(), apiFilm.getDirector(), apiFilm.getWriter())));
        }

        return films;
    }

    public void rating(Long filmId, Long userId, Double score) {
        this.validateParams(filmId, userId, score);

        Film film = this.findById(filmId);
        for (Rating r : film.getRatings()) {
            if (r.getUser().getId().equals(userId)) {
                throw new DuplicatedActionException("User with id = '" + userId + "' has already rated the film.");
            }
        }

        User user = this.updateUserScore(userService.findById(userId));

        ratingRepository.save(new Rating(null, film, user, score));
    }

    public void comment(Long filmId, Long userId, String message) {
        this.validateParams(filmId, userId, message);

        User user = userService.findById(userId);
        if (user.getProfile().equals(Profile.READER)) {
            throw new ProfileBlockException("You cannot comment with profile = '" + user.getProfile() + "'.");
        }

        Film film = this.findById(filmId);
        user = this.updateUserScore(user);

        commentRepository.save(new Comment(null, film, user, message));
    }

    public void commentResponse(Long commentId, Long userId, String message) {
        this.validateParams(commentId, userId, message);

        User user = userService.findById(userId);
        if (user.getProfile().equals(Profile.READER)) {
            throw new ProfileBlockException("You cannot reply to a comment with profile = '" + user.getProfile() + "'.");
        }

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment with id = '" + commentId + "' not found."));
        user = this.updateUserScore(user);

        commentResponseRepository.save(new CommentResponse(null, user, comment, message));
    }

    public void react(Long commentId, Long userId, Boolean reaction) {
        this.validateParams(commentId, userId, reaction);

        User user = userService.findById(userId);
        if (user.getProfile().equals(Profile.READER) || user.getProfile().equals(Profile.BASIC)) {
            throw new ProfileBlockException("You cannot react to a comment with profile = '" + user.getProfile() + "'.");
        }

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment with id = '" + commentId + "' not found."));
        for (Reaction r : comment.getReactions()) {
            if (r.getUser().getId().equals(userId)) {
                r.setReaction(reaction);
                reactionRepository.save(r);
                return;
            }
        }

        reactionRepository.save(new Reaction(null, user, comment, reaction));
    }

    public void deleteComment(Long commentId, Long userId) {
        this.validateParams(commentId, userId);

        User user = userService.findById(userId);
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

    public void setRepeatedComment(Long commentId, Long userId) {
        this.validateParams(commentId, userId);

        User user = userService.findById(userId);
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

    private void validateParams(Long id1, Long id2, Double score) {
        if (id1 == null || id2 == null || score == null) {
            throw new NullParameterException("You cannot rate with null parameters.");
        }
    }

    private void validateParams(Long id1, Long id2, String message) {
        if (id1 == null || id2 == null || Objects.equals(message, "")) {
            throw new NullParameterException("You cannot comment with null parameters.");
        }
    }

    private void validateParams(Long id1, Long id2, Boolean reaction) {
        if (id1 == null || id2 == null || reaction == null) {
            throw new NullParameterException("You cannot react with null parameters.");
        }
    }

    private void validateParams(Long id1, Long id2) {
        if (id1 == null || id2 == null) {
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

}
