package br.com.ialmeida.codingchallengeitau.repositories;

import br.com.ialmeida.codingchallengeitau.entities.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
}
