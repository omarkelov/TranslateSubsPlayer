package ru.nsu.fit.subsplayer.database.entities;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.InvalidObjectException;
import java.util.List;

@Entity
@Table(name = "movies")
@Getter @Setter @NoArgsConstructor
public class Movie {
    // todo @Unique
    // todo @NotNull
    // todo @Column(length = ?)

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    @Expose
    private String name;

    private String videoFilePath;

    @Expose
    private String lang;

    @Expose
    @Transient
    private String href;

    @Expose
    @Transient
    private List<Context> contexts;

    public Movie(Long userId, String name, String videoFilePath, String lang, List<Context> contexts) {
        this.userId = userId;
        this.name = name;
        this.videoFilePath = videoFilePath;
        this.lang = lang;
        this.contexts = contexts;
    }

    public void validate() throws InvalidObjectException {
        if (name == null)
            throw new InvalidObjectException("'name' parameter is not present");
        if (videoFilePath == null)
            throw new InvalidObjectException("'videoFilePath' parameter is not present");
        if (lang == null)
            throw new InvalidObjectException("'lang' parameter is not present");
    }
}
