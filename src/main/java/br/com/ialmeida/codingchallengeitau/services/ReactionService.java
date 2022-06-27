package br.com.ialmeida.codingchallengeitau.services;

import br.com.ialmeida.codingchallengeitau.entities.Reaction;
import br.com.ialmeida.codingchallengeitau.repositories.ReactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ReactionService {

    private final ReactionRepository reactionRepository;

    public void insert(Reaction reaction) {
        reactionRepository.save(reaction);
    }

    public void deleteAll(List<Reaction> reactions) {
        reactionRepository.deleteAll(reactions);
    }

}
