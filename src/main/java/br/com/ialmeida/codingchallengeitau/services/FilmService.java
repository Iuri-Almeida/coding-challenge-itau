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

    public void rating(Long filmId, String email, Double score) {
        this.validateParams(filmId, email, score);

        User user = userService.findByEmail(email);

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

    public void comment(Long filmId, String email, String message) {
        this.validateParams(filmId, email, message);

        User user = userService.findByEmail(email);
        if (user.getProfile().equals(Profile.READER)) {
            throw new ProfileBlockException("You cannot comment with profile = '" + user.getProfile() + "'.");
        }

        Film film = this.findById(filmId);
        userService.updateUserScore(user);

        commentService.insert(new Comment(null, film, user, message));
    }

    public void quoteComment(Long filmId, Long commentId, String email, String message) {
        this.validateParams(filmId, commentId, email, message);

        User user = userService.findByEmail(email);
        if (user.getProfile().equals(Profile.READER) || user.getProfile().equals(Profile.BASIC)) {
            throw new ProfileBlockException("You cannot quote a comment with profile = '" + user.getProfile() + "'.");
        }

        Comment comment = commentService.findById(commentId);
        message = "@" + comment.getUser().getName() + ": '" + comment.getMessage() + "' \n\n" + message;

        this.comment(filmId, email, message);
    }

    public void commentResponse(Long commentId, String email, String message) {
        this.validateParams(commentId, email, message);

        User user = userService.findByEmail(email);
        if (user.getProfile().equals(Profile.READER)) {
            throw new ProfileBlockException("You cannot reply to a comment with profile = '" + user.getProfile() + "'.");
        }

        Comment comment = commentService.findById(commentId);
        userService.updateUserScore(user);

        commentResponseService.insert(new CommentResponse(null, user, comment, message));
    }

    public void react(Long commentId, String email, Boolean reaction) {
        this.validateParams(commentId, email, reaction);

        User user = userService.findByEmail(email);
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

    public void deleteComment(Long commentId, String email) {
        this.validateParams(commentId, email);

        User user = userService.findByEmail(email);
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

    public void setRepeatedComment(Long commentId, String email) {
        this.validateParams(commentId, email);

        User user = userService.findByEmail(email);
        if (user.getProfile().equals(Profile.READER) || user.getProfile().equals(Profile.BASIC) || user.getProfile().equals(Profile.ADVANCED)) {
            throw new ProfileBlockException("You cannot set a comment as repeated with profile = '" + user.getProfile() + "'.");
        }

        Comment comment = commentService.findById(commentId);

        comment.setIsRepeated(true);

        commentService.insert(comment);
    }

    public void makeModerator(Long userId, String email) {
        this.validateParams(userId, email);

        User fromUser = userService.findByEmail(email);
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

    private void validateParams(Long id, String email, Double score) {
        if (id == null || Objects.equals(email, "") || score == null) {
            throw new NullParameterException("You cannot rate with null parameters.");
        }
    }

    private void validateParams(Long id, String email, String message) {
        if (id == null || Objects.equals(email, "") || Objects.equals(message, "")) {
            throw new NullParameterException("You cannot comment with null parameters.");
        }
    }

    private void validateParams(Long id1, Long id2, String email, String message) {
        if (id1 == null || id2 == null || Objects.equals(email, "") || Objects.equals(message, "")) {
            throw new NullParameterException("You cannot quote a comment with null parameters.");
        }
    }

    private void validateParams(Long id, String email, Boolean reaction) {
        if (id == null || Objects.equals(email, "") || reaction == null) {
            throw new NullParameterException("You cannot react with null parameters.");
        }
    }

    private void validateParams(Long id, String email) {
        if (id == null || Objects.equals(email, "")) {
            throw new NullParameterException("You cannot make moderator, delete or set a comment as repeated with null parameters.");
        }
    }

    @Transactional
    protected Film insert(Film film) {
        return filmRepository.save(film);
    }

}
