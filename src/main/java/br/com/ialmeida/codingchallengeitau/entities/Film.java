package br.com.ialmeida.codingchallengeitau.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "film")
    private List<Comment> comments;

    @OneToMany(mappedBy = "film")
    private List<Rating> ratings;

    private Double rating;

    public Film(Long id, String title, String genre, String director, String writer) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.director = director;
        this.writer = writer;
        this.comments = new ArrayList<>();
        this.ratings = new ArrayList<>();
    }

    public Double getRating() {
        double sumRatings = 0.0;
        int totalRatings = ratings.size();

        for (Rating r : ratings) {
            sumRatings += r.getScore();
        }

        return (totalRatings == 0) ? 0.0 : Math.round((sumRatings / totalRatings) * 100.0) / 100.0;
    }

}
