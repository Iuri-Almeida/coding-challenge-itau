package br.com.ialmeida.codingchallengeitau.entities;

import br.com.ialmeida.codingchallengeitau.entities.enums.Profile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer score;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Comment> comments;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Rating> ratings;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Profile profile;

    public User(Long id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.score = 0;
        this.comments = new ArrayList<>();
        this.ratings = new ArrayList<>();
        this.profile = Profile.READER;
    }

    public void addScore() {
        score++;

        if (score >= 20 && score < 100) {
            this.setProfile(Profile.BASIC);
        } else if (score >= 100 && score < 1000) {
            this.setProfile(Profile.ADVANCED);
        } else if (score >= 1000) {
            this.setProfile(Profile.MODERATOR);
        }
    }

}
