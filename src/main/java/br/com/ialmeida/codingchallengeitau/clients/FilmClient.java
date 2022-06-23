package br.com.ialmeida.codingchallengeitau.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(url = "https://www.omdbapi.com/?apikey=51dc6e44", name = "films")
public interface FilmClient {

    @GetMapping
    Object findFilmByTitle(@RequestParam(value = "t") String title);

}
