package br.com.ialmeida.codingchallengeitau.config;

import br.com.ialmeida.codingchallengeitau.entities.Comment;
import br.com.ialmeida.codingchallengeitau.entities.Film;
import br.com.ialmeida.codingchallengeitau.entities.Rating;
import br.com.ialmeida.codingchallengeitau.entities.User;
import br.com.ialmeida.codingchallengeitau.repositories.CommentRepository;
import br.com.ialmeida.codingchallengeitau.repositories.FilmRepository;
import br.com.ialmeida.codingchallengeitau.repositories.RatingRepository;
import br.com.ialmeida.codingchallengeitau.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;

@Configuration
@Profile(value = "test")
@AllArgsConstructor
public class TestConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final CommentRepository commentRepository;
    private final RatingRepository ratingRepository;

    @Override
    public void run(String... args) throws Exception {

        User u1 = new User(null, "Iuri", "iuri@gmail.com", "123456");
        User u2 = new User(null, "John", "john@gmail.com", "654321");

        userRepository.saveAll(Arrays.asList(u1, u2));

        Film f1 = new Film(null, "Title 1", "Genre 1", "Director 1", "Writer 1");
        Film f2 = new Film(null, "Title 2", "Genre 2", "Director 2", "Writer 2");

        filmRepository.saveAll(Arrays.asList(f1, f2));

        Comment c1 = new Comment(null, f1, u2, "First message (U2)");
        Comment c2 = new Comment(null, f2, u2, "Second message (U2)");
        Comment c3 = new Comment(null, f2, u2, "Third message (U2)");
        Comment c4 = new Comment(null, f1, u1, "Fourth message (U1)");

        commentRepository.saveAll(Arrays.asList(c1, c2, c3, c4));

        Rating r1 = new Rating(null, f1, u1, 2.0);
        Rating r2 = new Rating(null, f2, u1, 5.0);
        Rating r3 = new Rating(null, f2, u2, 8.0);

        ratingRepository.saveAll(Arrays.asList(r1, r2, r3));

    }
}
