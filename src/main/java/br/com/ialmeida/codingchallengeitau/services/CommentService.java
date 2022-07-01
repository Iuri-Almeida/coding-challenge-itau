package br.com.ialmeida.codingchallengeitau.services;

import br.com.ialmeida.codingchallengeitau.entities.Comment;
import br.com.ialmeida.codingchallengeitau.exceptions.ResourceNotFoundException;
import br.com.ialmeida.codingchallengeitau.repositories.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment findById(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Comment with id = '" + id + "' not found."));
    }

    @Transactional
    public void insert(Comment comment) {
        commentRepository.save(comment);
    }

    @Transactional
    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }

}
