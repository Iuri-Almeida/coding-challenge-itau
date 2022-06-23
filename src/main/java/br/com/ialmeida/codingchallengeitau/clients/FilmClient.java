package br.com.ialmeida.codingchallengeitau.clients;

import br.com.ialmeida.codingchallengeitau.entities.Film;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(url = "https://www.omdbapi.com/?apikey=51dc6e44", name = "films")
public interface FilmClient {

    @GetMapping
    Film findByTitle(@RequestParam(value = "t") String title);

}
