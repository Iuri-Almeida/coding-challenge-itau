package br.com.ialmeida.codingchallengeitau.services;

import br.com.ialmeida.codingchallengeitau.config.PropertiesConfig;
import br.com.ialmeida.codingchallengeitau.entities.User;
import br.com.ialmeida.codingchallengeitau.exceptions.DuplicatedActionException;
import br.com.ialmeida.codingchallengeitau.exceptions.JwtAuthenticationException;
import br.com.ialmeida.codingchallengeitau.exceptions.NullParameterException;
import br.com.ialmeida.codingchallengeitau.exceptions.ResourceNotFoundException;
import br.com.ialmeida.codingchallengeitau.repositories.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final PropertiesConfig propertiesConfig;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with id = '" + id + "' not found."));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User with email = '" + email + "' not found."));
    }

    public User insert(User user) {
        this.validateParams(user);
        this.validateUser(user);

        user.setPassword(encoder.encode(user.getPassword()));

        return this.updateUser(new User(null, user.getName(), user.getEmail(), user.getPassword()));
    }

    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void updateUserScore(User user) {
        user.addScore();
        this.updateUser(user);
    }

    public User getUserByToken(String token) {
        try {
            String email = JWT.require(Algorithm.HMAC512(propertiesConfig.getTokenSecret()))
                    .build()
                    .verify(token)
                    .getSubject();
            return this.findByEmail(email);
        } catch (SignatureVerificationException e) {
            throw new JwtAuthenticationException("You must use a valid JWT token.");
        }
    }

    private void validateParams(User user) {
        if (user.getName() == null || user.getEmail() == null || user.getPassword() == null) {
            throw new NullParameterException("You cannot save user with null parameters.");
        }
    }

    private void validateUser(User user) {
        Optional<User> dbUser = userRepository.findByEmail(user.getEmail());
        if (dbUser.isPresent()) {
            throw new DuplicatedActionException("There is already a user with e-mail = '" + user.getEmail() + "'.");
        }
    }

}
