package br.com.ialmeida.codingchallengeitau.repositories;

import br.com.ialmeida.codingchallengeitau.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}
