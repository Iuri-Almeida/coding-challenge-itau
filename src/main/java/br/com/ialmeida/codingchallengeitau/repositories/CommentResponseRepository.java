package br.com.ialmeida.codingchallengeitau.repositories;

import br.com.ialmeida.codingchallengeitau.entities.CommentResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentResponseRepository extends JpaRepository<CommentResponse, Long> {
}
