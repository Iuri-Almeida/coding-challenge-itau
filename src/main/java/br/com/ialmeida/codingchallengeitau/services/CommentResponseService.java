package br.com.ialmeida.codingchallengeitau.services;

import br.com.ialmeida.codingchallengeitau.entities.CommentResponse;
import br.com.ialmeida.codingchallengeitau.repositories.CommentResponseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
public class CommentResponseService {

    private final CommentResponseRepository commentResponseRepository;

    @Transactional
    public void insert(CommentResponse commentResponse) {
        commentResponseRepository.save(commentResponse);
    }

    @Transactional
    public void deleteAll(List<CommentResponse> commentResponses) {
        commentResponseRepository.deleteAll(commentResponses);
    }

}
