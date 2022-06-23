package br.com.ialmeida.codingchallengeitau.repositories;

import br.com.ialmeida.codingchallengeitau.entities.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {

    List<Film> findByTitleContainingIgnoreCase(String text);

}
