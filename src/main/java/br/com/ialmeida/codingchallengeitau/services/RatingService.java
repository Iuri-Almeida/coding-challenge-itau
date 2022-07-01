package br.com.ialmeida.codingchallengeitau.services;

import br.com.ialmeida.codingchallengeitau.entities.Rating;
import br.com.ialmeida.codingchallengeitau.repositories.RatingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;

    @Transactional
    public void insert(Rating rating) {
        ratingRepository.save(rating);
    }

}
