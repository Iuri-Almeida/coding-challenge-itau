package br.com.ialmeida.codingchallengeitau.services;

import br.com.ialmeida.codingchallengeitau.entities.Rating;
import br.com.ialmeida.codingchallengeitau.repositories.RatingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;

    public void insert(Rating rating) {
        ratingRepository.save(rating);
    }

}
