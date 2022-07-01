package br.com.ialmeida.codingchallengeitau.services;

import br.com.ialmeida.codingchallengeitau.clients.FilmClient;
import br.com.ialmeida.codingchallengeitau.config.PropertiesConfig;
import br.com.ialmeida.codingchallengeitau.entities.*;
import br.com.ialmeida.codingchallengeitau.entities.enums.Profile;
import br.com.ialmeida.codingchallengeitau.exceptions.*;
import br.com.ialmeida.codingchallengeitau.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class FilmService {

    private final PropertiesConfig propertiesConfig;

    private final FilmRepository filmRepository;
    private final FilmClient filmClient;

    private final CommentResponseService commentResponseService;
    private final CommentService commentService;
    private final RatingService ratingService;
    private final ReactionService reactionService;
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
            Film apiFilm = filmClient.findByTitle(title, propertiesConfig.getApiKey());
            if (apiFilm.getTitle() == null && apiFilm.getGenre() == null && apiFilm.getDirector() == null && apiFilm.getWriter() == null) {
                throw new ResourceNotFoundException("Film with title = " + title + "' not found.");
            }

            films.add(this.insert(new Film(null, apiFilm.getTitle(), apiFilm.getGenre(), apiFilm.getDirector(), apiFilm.getWriter())));
        }

        return films;
    }

    public void rating(Long filmId, String token, Double score) {
        this.validateParams(filmId, token, score);

        User user = userService.getUserByToken(token);

        Film film = this.findById(filmId);
        for (Rating r : film.getRatings()) {
            if (r.getUser().equals(user)) {
                r.setScore(score);
                ratingService.insert(r);
                return;
            }
        }

        userService.updateUserScore(user);

        ratingService.insert(new Rating(null, film, user, score));
    }

    public void comment(Long filmId, String token, String message) {
        this.validateParams(filmId, token, message);

        User user = userService.getUserByToken(token);
        if (user.getProfile().equals(Profile.READER)) {
            throw new ProfileBlockException("You cannot comment with profile = '" + user.getProfile() + "'.");
        }

        Film film = this.findById(filmId);
        userService.updateUserScore(user);

        commentService.insert(new Comment(null, film, user, message));
    }

    public void quoteComment(Long filmId, Long commentId, String token, String message) {
        this.validateParams(filmId, commentId, token, message);

        User user = userService.getUserByToken(token);
        if (user.getProfile().equals(Profile.READER) || user.getProfile().equals(Profile.BASIC)) {
            throw new ProfileBlockException("You cannot quote a comment with profile = '" + user.getProfile() + "'.");
        }

        Comment comment = commentService.findById(commentId);
        message = "@" + comment.getUser().getName() + ": '" + comment.getMessage() + "' \n\n" + message;

        this.comment(filmId, token, message);
    }

    public void commentResponse(Long commentId, String token, String message) {
        this.validateParams(commentId, token, message);

        User user = userService.getUserByToken(token);
        if (user.getProfile().equals(Profile.READER)) {
            throw new ProfileBlockException("You cannot reply to a comment with profile = '" + user.getProfile() + "'.");
        }

        Comment comment = commentService.findById(commentId);
        userService.updateUserScore(user);

        commentResponseService.insert(new CommentResponse(null, user, comment, message));
    }

    public void react(Long commentId, String token, Boolean reaction) {
        this.validateParams(commentId, token, reaction);

        User user = userService.getUserByToken(token);
        if (user.getProfile().equals(Profile.READER) || user.getProfile().equals(Profile.BASIC)) {
            throw new ProfileBlockException("You cannot react to a comment with profile = '" + user.getProfile() + "'.");
        }

        Comment comment = commentService.findById(commentId);
        for (Reaction r : comment.getReactions()) {
            if (r.getUser().equals(user)) {
                r.setReaction(reaction);
                reactionService.insert(r);
                return;
            }
        }

        reactionService.insert(new Reaction(null, user, comment, reaction));
    }

    public void deleteComment(Long commentId, String token) {
        this.validateParams(commentId, token);

        User user = userService.getUserByToken(token);
        if (user.getProfile().equals(Profile.READER) || user.getProfile().equals(Profile.BASIC) || user.getProfile().equals(Profile.ADVANCED)) {
            throw new ProfileBlockException("You cannot delete a comment with profile = '" + user.getProfile() + "'.");
        }

        Comment comment = commentService.findById(commentId);

        try {
            commentResponseService.deleteAll(comment.getCommentResponses());
            reactionService.deleteAll(comment.getReactions());

            commentService.delete(comment);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Error deleting comment. Could not found all resources");
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Resource with dependencies in database");
        }
    }

    public void setRepeatedComment(Long commentId, String token) {
        this.validateParams(commentId, token);

        User user = userService.getUserByToken(token);
        if (user.getProfile().equals(Profile.READER) || user.getProfile().equals(Profile.BASIC) || user.getProfile().equals(Profile.ADVANCED)) {
            throw new ProfileBlockException("You cannot set a comment as repeated with profile = '" + user.getProfile() + "'.");
        }

        Comment comment = commentService.findById(commentId);

        comment.setIsRepeated(true);

        commentService.insert(comment);
    }

    public void makeModerator(Long userId, String token) {
        this.validateParams(userId, token);

        User fromUser = userService.getUserByToken(token);
        if (fromUser.getProfile() != Profile.MODERATOR) {
            throw new ProfileBlockException("You cannot make someone else a moderator with profile = '" + fromUser.getProfile() + "'.");
        }

        User toUser = userService.findById(userId);
        toUser.setProfile(Profile.MODERATOR);

        userService.updateUser(toUser);
    }

    private void validateParams(String title) {
        if (Objects.equals(title, "")) {
            throw new NullParameterException("You cannot search with null parameters.");
        }
    }

    private void validateParams(Long id, String token, Double score) {
        if (id == null || Objects.equals(token, "") || score == null) {
            throw new NullParameterException("You cannot rate with null parameters.");
        }
    }

    private void validateParams(Long id, String token, String message) {
        if (id == null || Objects.equals(token, "") || Objects.equals(message, "")) {
            throw new NullParameterException("You cannot comment with null parameters.");
        }
    }

    private void validateParams(Long id1, Long id2, String token, String message) {
        if (id1 == null || id2 == null || Objects.equals(token, "") || Objects.equals(message, "")) {
            throw new NullParameterException("You cannot quote a comment with null parameters.");
        }
    }

    private void validateParams(Long id, String token, Boolean reaction) {
        if (id == null || Objects.equals(token, "") || reaction == null) {
            throw new NullParameterException("You cannot react with null parameters.");
        }
    }

    private void validateParams(Long id, String token) {
        if (id == null || Objects.equals(token, "")) {
            throw new NullParameterException("You cannot make moderator, delete or set a comment as repeated with null parameters.");
        }
    }

    @Transactional
    protected Film insert(Film film) {
        return filmRepository.save(film);
    }

}
