package br.com.ialmeida.codingchallengeitau.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "tb_film")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty(value = "Title")
    private String title;

    @JsonProperty(value = "Genre")
    private String genre;

    @JsonProperty(value = "Director")
    private String director;

    @JsonProperty(value = "Writer")
    private String writer;

}
