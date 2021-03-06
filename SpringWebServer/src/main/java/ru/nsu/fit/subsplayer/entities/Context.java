package ru.nsu.fit.subsplayer.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Context {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long MovieId;
    private String context;
    private String link;

    public Context() {}

    public Context(Long movieId, String context, String link) {
        MovieId = movieId;
        this.context = context;
        this.link = link;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMovieId() {
        return MovieId;
    }

    public void setMovieId(Long movieId) {
        MovieId = movieId;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
