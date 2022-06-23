package br.com.ialmeida.codingchallengeitau.services;

import br.com.ialmeida.codingchallengeitau.clients.FilmClient;
import br.com.ialmeida.codingchallengeitau.entities.Film;
import br.com.ialmeida.codingchallengeitau.repositories.FilmRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FilmService {

    private final FilmRepository filmRepository;
    private final FilmClient filmClient;

    public List<Film> findAll() {
        return filmRepository.findAll();
    }

    public List<Film> findByTitle(String title) {
        List<Film> films = filmRepository.findByTitleContainingIgnoreCase(title);

        if (films.isEmpty()) {
            Film apiFilm = filmClient.findByTitle(title);
            films.add(this.insert(new Film(null, apiFilm.getTitle(), apiFilm.getGenre(), apiFilm.getDirector(), apiFilm.getWriter())));
        }

        return films;
    }

    private Film insert(Film film) {
        return filmRepository.save(film);
    }

}
