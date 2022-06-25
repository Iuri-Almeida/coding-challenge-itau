package br.com.ialmeida.codingchallengeitau.details.services.impl;

import br.com.ialmeida.codingchallengeitau.details.UserDetail;
import br.com.ialmeida.codingchallengeitau.entities.User;
import br.com.ialmeida.codingchallengeitau.exceptions.ResourceNotFoundException;
import br.com.ialmeida.codingchallengeitau.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> dbUser = userRepository.findByEmail(email);
        if (dbUser.isEmpty()) {
            throw new ResourceNotFoundException("User with e-mail = '" + email + "' not found.");
        }
        return new UserDetail(dbUser.get());
    }
}
