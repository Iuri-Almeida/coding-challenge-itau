package br.com.ialmeida.codingchallengeitau.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private Integer score;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Comment> comments;

    public User(Long id, String name, String email, String password, Integer score) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.score = score;
        this.comments = new ArrayList<>();
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

}
