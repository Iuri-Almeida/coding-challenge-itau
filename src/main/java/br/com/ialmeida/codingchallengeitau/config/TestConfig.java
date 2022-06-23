package br.com.ialmeida.codingchallengeitau.config;

import br.com.ialmeida.codingchallengeitau.entities.User;
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

    @Override
    public void run(String... args) throws Exception {

        User u1 = new User(null, "Iuri", "iuri@gmail.com", "123456", 23);
        User u2 = new User(null, "John", "john@gmail.com", "654321", 0);

        userRepository.saveAll(Arrays.asList(u1, u2));

    }
}
